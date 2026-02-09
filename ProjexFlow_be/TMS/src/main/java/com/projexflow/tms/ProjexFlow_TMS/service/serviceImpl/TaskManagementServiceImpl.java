package com.projexflow.tms.ProjexFlow_TMS.service.serviceImpl;

import com.projexflow.tms.ProjexFlow_TMS.dto.AssignTaskRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.AssignTaskResultResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.ReviewSubmissionRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.SubmitTaskRequest;
import com.projexflow.tms.ProjexFlow_TMS.entity.*;
import com.projexflow.tms.ProjexFlow_TMS.exception.BadRequestException;
import com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException;
import com.projexflow.tms.ProjexFlow_TMS.exception.NotFoundException;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskAssignmentRepository;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskReviewRepository;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskSubmissionRepository;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskTemplateRepository;
import com.projexflow.tms.ProjexFlow_TMS.service.ExternalIntegrationService;
import com.projexflow.tms.ProjexFlow_TMS.service.TaskManagementService;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.NotificationCreateRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.CreateActivityLogRequest;
import com.projexflow.tms.ProjexFlow_TMS.service.ActivityLogClient;
import com.projexflow.tms.ProjexFlow_TMS.service.NotificationClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskManagementServiceImpl implements TaskManagementService{

    private final TaskTemplateRepository taskTemplateRepo;
    private final TaskAssignmentRepository assignmentRepo;
    private final TaskSubmissionRepository submissionRepo;
    private final TaskReviewRepository reviewRepo;
    private final ExternalIntegrationService externalService;
    private final NotificationClient notificationClient;
    private final ActivityLogClient activityLogClient;

    /**
     * Assign a task to specific group(s).
     *
     * How permission is validated:
     * - We fetch allowed groups under this mentor from externalService (MAMS dummy now, real later)
     * - We assign only to intersection of requested groupIds and allowed groupIds
     *
     * Idempotency:
     * - Unique(task_id, group_id) constraint ensures duplicates don't happen
     */
    @Transactional
    public AssignTaskResultResponse assignToGroups(Long mentorId, Long taskId, AssignTaskRequest req) {

        TaskTemplate template = taskTemplateRepo.findByIdAndMentorIdAndBatchId(taskId, mentorId, req.batchId())
                .orElseThrow(() -> new NotFoundException("Task template not found for this mentor/batch"));
        Set<Long> allowedGroupIds = externalService.getGroupIdsForMentor(mentorId, req.batchId());
        if (allowedGroupIds == null || allowedGroupIds.isEmpty()) {
            throw new ForbiddenException("No groups are assigned to this mentor for the given batch");
        }

        // remove duplicates in request
        Set<Long> requestedGroupIds = new HashSet<>(req.groupIds());

        long created = 0;
        long skipped = 0;

        for (Long groupId : requestedGroupIds) {

            // if group not under this mentor, skip
            if (!allowedGroupIds.contains(groupId)) {
                skipped++;
                continue;
            }

            TaskAssignment assignment = TaskAssignment.builder()
                    .task(template)
                    .batchId(req.batchId())
                    .mentorId(mentorId)
                    .groupId(groupId)
                    .assignedAt(LocalDateTime.now())
                    .dueAt(req.dueAt() != null ? req.dueAt() : template.getDefaultDueAt())
                    .assignmentStatus(AssignmentStatus.ASSIGNED)
                    .state(AssignmentState.NOT_SUBMITTED)
                    .build();

            try {
                assignmentRepo.save(assignment);
                created++;

                // 🔔 Notify group members: mentor assigned a task to the group
                try {
                    Set<Long> studentIds = externalService.getStudentIdsForGroup(groupId);
                    if (studentIds != null) {
                        for (Long sid : studentIds) {
                            notificationClient.create(new NotificationCreateRequest(
                                    sid,
                                    "STUDENT",
                                    "TASK_ASSIGNED",
                                    "New task assigned",
                                    "A new task has been assigned to your group: " + template.getTitle(),
                                    "TASK_ASSIGNMENT",
                                    assignment.getId(),
                                    java.util.Map.of(
                                            "taskId", template.getId(),
                                            "groupId", groupId,
                                            "batchId", req.batchId(),
                                            "mentorId", mentorId
                                    )
                            ));
                        }
                    }
                } catch (Exception ignored) {
                }
            } catch (DataIntegrityViolationException ex) {
                // Unique(task_id, group_id) violation => already assigned
                skipped++;
            }
        }

        return new AssignTaskResultResponse(created, skipped);
    }

    @Transactional
    public AssignTaskResultResponse assignToAllGroups(Long mentorId, Long taskId, Long batchId, LocalDateTime dueAt) {
        // fetch all mentor groups via ExternalIntegrationService
        Set<Long> allowedGroupIds = externalService.getGroupIdsForMentor(mentorId, batchId);

        if (allowedGroupIds == null || allowedGroupIds.isEmpty()) {
            throw new ForbiddenException("No groups are assigned to this mentor for the given batch");
        }

        AssignTaskRequest req = new AssignTaskRequest(batchId, allowedGroupIds.stream().toList(), dueAt);
        return assignToGroups(mentorId, taskId, req);
    }


    /**
     * Student submits task for a group assignment.
     *
     * Validations:
     * - Assignment must exist and not be cancelled
     * - Header groupId must match assignment.groupId
     * - Optional but recommended: validate student belongs to group using externalService (GMS dummy now, real later)
     * - If repoUrl is not provided, auto-fetch repoUrl from externalService (PMS dummy now, real later)
     */
    @Transactional
    public Long submitTask(Long studentId, Long assignmentId, SubmitTaskRequest req) {

        TaskAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        if (assignment.getAssignmentStatus() == AssignmentStatus.CANCELLED) {
            throw new BadRequestException("This assignment has been cancelled");
        }

        Long studentGroupId = externalService.getGroupIdForStudent(assignment.getBatchId(), studentId);
        if (studentGroupId == null) {
            throw new ForbiddenException("Student is not assigned to any active group for this batch");
        }
        if (!assignment.getGroupId().equals(studentGroupId)) {
            throw new ForbiddenException("You cannot submit for another group");
        }

        // block if already verified/rejected (your choice)
        if (assignment.getState() == AssignmentState.VERIFIED) {
            throw new BadRequestException("Task already verified. No further submissions allowed.");
        }
        if (assignment.getState() == AssignmentState.REJECTED) {
            throw new BadRequestException("Task rejected. Contact mentor.");
        }

        // ✅ OPTIONAL (recommended): validate student belongs to group (GMS in future)
        Set<Long> groupStudents = externalService.getStudentIdsForGroup(studentGroupId);
        if (groupStudents != null && !groupStudents.isEmpty() && !groupStudents.contains(studentId)) {
            throw new ForbiddenException("Student does not belong to this group");
        }

        // ✅ Auto-fetch repoUrl if missing (PMS in future)
        String repoUrl = req.repoUrl();
        if (repoUrl == null || repoUrl.isBlank()) {
            repoUrl = externalService.getRepoUrlForGroup(assignment.getBatchId(), assignment.getGroupId());
        }

        // If still null, you can choose to allow submission or block. I recommend blocking:
        if (repoUrl == null || repoUrl.isBlank()) {
            throw new BadRequestException("repoUrl is required (either provide it or create project in PMS)");
        }

        TaskSubmission submission = TaskSubmission.builder()
                .assignment(assignment)
                .batchId(assignment.getBatchId())
                .groupId(assignment.getGroupId())
                .submittedByStudentId(studentId)
                .repoUrl(repoUrl)                 // ✅ uses fetched or provided repoUrl
                .prUrl(req.prUrl())
                .submittedAt(LocalDateTime.now())
                .build();

        submission = submissionRepo.save(submission);

        // Update assignment state for mentor dashboard
        assignment.setState(AssignmentState.PENDING_REVIEW);
        assignmentRepo.save(assignment);

        // 🔔 Notify mentor: group submitted work for review
        try {
            notificationClient.create(new NotificationCreateRequest(
                    assignment.getMentorId(),
                    "MENTOR",
                    "TASK_SUBMITTED",
                    "Task submission received",
                    "Group " + assignment.getGroupId() + " submitted work for: " + assignment.getTask().getTitle(),
                    "TASK_SUBMISSION",
                    submission.getId(),
                    java.util.Map.of(
                            "assignmentId", assignment.getId(),
                            "groupId", assignment.getGroupId(),
                            "batchId", assignment.getBatchId(),
                            "submittedByStudentId", studentId
                    )
            ));
        } catch (Exception ignored) {
        }

        // 🧾 Activity log: student submitted a task
        try {
            activityLogClient.create(CreateActivityLogRequest.builder()
                    .sourceService("TMS")
                    .action("SUBMIT_TASK")
                    .entityType("TASK_SUBMISSION")
                    .entityId(String.valueOf(submission.getId()))
                    .description("Submitted work for task '" + assignment.getTask().getTitle() + "' (assignmentId=" + assignment.getId() + ")")
                    .metadata(java.util.Map.of(
                            "submissionId", submission.getId(),
                            "assignmentId", assignment.getId(),
                            "taskId", assignment.getTask().getId(),
                            "taskTitle", assignment.getTask().getTitle(),
                            "groupId", assignment.getGroupId(),
                            "batchId", assignment.getBatchId(),
                            "mentorId", assignment.getMentorId(),
                            "submittedByStudentId", studentId,
                            "repoUrl", submission.getRepoUrl(),
                            "prUrl", submission.getPrUrl()
                    ))
                    .build());
        } catch (Exception ignored) {
        }

        return submission.getId();
    }
    @Transactional
    public Long reviewAssignment(Long mentorId, Long assignmentId, ReviewSubmissionRequest req) {

        TaskAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        if (!assignment.getMentorId().equals(mentorId)) {
            throw new ForbiddenException("You are not allowed to review this assignment");
        }
        if (assignment.getAssignmentStatus() == AssignmentStatus.CANCELLED) {
            throw new BadRequestException("Assignment is cancelled");
        }

        // Ensure there is at least one submission to review
        TaskSubmission latestSubmission = submissionRepo.findTopByAssignment_IdOrderBySubmittedAtDesc(assignmentId)
                .orElseThrow(() -> new BadRequestException("No submission found to review"));

        // keep clean: VERIFIED should not include a failure reason
        if (req.decision() == ReviewDecision.VERIFIED && req.reasonCode() != null) {
            throw new BadRequestException("Reason code is not allowed when decision is VERIFIED");
        }

        TaskReview review = TaskReview.builder()
                .assignmentId(assignmentId)
                .mentorId(mentorId)
                .decision(req.decision())
                .reasonCode(req.reasonCode())
                .comments(req.comments())
                .reviewedAt(LocalDateTime.now())
                .build();

        review = reviewRepo.save(review);

        switch (req.decision()) {
            case VERIFIED -> assignment.setState(AssignmentState.VERIFIED);
            case CHANGES_REQUESTED -> assignment.setState(AssignmentState.CHANGES_REQUESTED);
            case REJECTED -> assignment.setState(AssignmentState.REJECTED);
            default -> throw new BadRequestException("Unsupported decision");
        }

        assignmentRepo.save(assignment);

        // 🔔 Notify group members: mentor reviewed the submission
        try {
            Set<Long> studentIds = externalService.getStudentIdsForGroup(assignment.getGroupId());
            if (studentIds != null) {
                String msg = "Mentor reviewed your submission for: " + assignment.getTask().getTitle()
                        + " (" + req.decision() + ")";
                for (Long sid : studentIds) {
                    notificationClient.create(new NotificationCreateRequest(
                            sid,
                            "STUDENT",
                            "TASK_REVIEWED",
                            "Submission reviewed",
                            msg,
                            "TASK_REVIEW",
                            review.getId(),
                            java.util.Map.of(
                                    "assignmentId", assignment.getId(),
                                    "groupId", assignment.getGroupId(),
                                    "batchId", assignment.getBatchId(),
                                    "decision", req.decision().toString(),
                                    "reasonCode", req.reasonCode() != null ? req.reasonCode().toString() : null
                            )
                    ));
                }
            }
        } catch (Exception ignored) {
        }
        return review.getId();
    }

    // ----------------- Read helpers used by controllers -----------------

    public List<TaskSubmission> getSubmissionsForMentor(Long mentorId, Long assignmentId) {
        TaskAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        if (!assignment.getMentorId().equals(mentorId)) {
            throw new ForbiddenException("Not allowed");
        }
        return submissionRepo.findAllByAssignment_IdOrderBySubmittedAtDesc(assignmentId);
    }

    public List<TaskSubmission> getSubmissionsForStudent(Long studentId, Long assignmentId) {
        TaskAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        Long studentGroupId = externalService.getGroupIdForStudent(assignment.getBatchId(), studentId);
        if (studentGroupId == null) {
            throw new ForbiddenException("Student is not assigned to any active group for this batch");
        }

        if (!assignment.getGroupId().equals(studentGroupId)) {
            throw new ForbiddenException("Not allowed");
        }
        return submissionRepo.findAllByAssignment_IdOrderBySubmittedAtDesc(assignmentId);
    }
}


