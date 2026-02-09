package com.projexflow.tms.ProjexFlow_TMS.controller;

import com.projexflow.tms.ProjexFlow_TMS.dto.IdResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.CountResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.SubmitTaskRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.TaskAssignmentResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.TaskAssignmentDetailResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.TaskSubmissionResponse;
import com.projexflow.tms.ProjexFlow_TMS.identity.CurrentUserResolver;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentStatus;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentState;
import com.projexflow.tms.ProjexFlow_TMS.entity.TaskAssignment;
import com.projexflow.tms.ProjexFlow_TMS.entity.TaskSubmission;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskAssignmentRepository;
import com.projexflow.tms.ProjexFlow_TMS.service.TaskManagementService;
import com.projexflow.tms.ProjexFlow_TMS.service.ExternalIntegrationService;
import com.projexflow.tms.ProjexFlow_TMS.service.UmsClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/tms/student")
@RequiredArgsConstructor
public class StudentTaskController {

    private final TaskAssignmentRepository assignmentRepo;
    private final TaskManagementService taskService;
    private final ExternalIntegrationService externalService;
    private final UmsClient umsClient;
    private final CurrentUserResolver currentUser;

    @GetMapping("/assignments")
    public List<TaskAssignmentResponse> listMyAssignments(
    ) {
        var student = currentUser.student();
        Long studentId = student.getId();
        Long batchId = student.getBatchId();
        Long groupId = externalService.getGroupIdForStudent(batchId, studentId);
        if (groupId == null) {
            throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException("Student is not assigned to any active group for this batch");
        }

        List<TaskAssignment> assignments = assignmentRepo
                .findAllByGroupIdAndBatchIdAndAssignmentStatusOrderByUpdatedAtDesc(groupId, batchId, AssignmentStatus.ASSIGNED);

        // Enrich mentor profiles for UI
        List<Long> mentorIds = assignments.stream()
                .map(TaskAssignment::getMentorId)
                .distinct()
                .toList();
        Map<Long, com.projexflow.tms.ProjexFlow_TMS.dto.external.MentorResponse> mentorsById = umsClient
                .mentorsByIds(mentorIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.projexflow.tms.ProjexFlow_TMS.dto.external.MentorResponse::id,
                        Function.identity()
                ));

        return assignments.stream().map(a -> new TaskAssignmentResponse(
                a.getId(),
                a.getTask().getId(),
                a.getTask().getTitle(),
                a.getBatchId(),
                a.getMentorId(),
                mentorsById.get(a.getMentorId()),
                a.getGroupId(),
                a.getAssignedAt(),
                a.getDueAt(),
                a.getAssignmentStatus(),
                a.getState(),
                a.getUpdatedAt()
        )).toList();
    }

    /** Dashboard metric: number of tasks currently assigned to my group (ASSIGNED). */
    @GetMapping("/stats/assigned-tasks/count")
    public CountResponse assignedTasksCount() {
        var student = currentUser.student();
        Long studentId = student.getId();
        Long batchId = student.getBatchId();
        Long groupId = externalService.getGroupIdForStudent(batchId, studentId);
        if (groupId == null) {
            throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException(
                    "Student is not assigned to any active group for this batch"
            );
        }
        long count = assignmentRepo.countByGroupIdAndBatchIdAndAssignmentStatus(groupId, batchId, AssignmentStatus.ASSIGNED);
        return new CountResponse(count);
    }

    /** Dashboard metric: number of VERIFIED tasks for my group. */
    @GetMapping("/stats/verified-tasks/count")
    public CountResponse verifiedTasksCount() {
        var student = currentUser.student();
        Long studentId = student.getId();
        Long batchId = student.getBatchId();
        Long groupId = externalService.getGroupIdForStudent(batchId, studentId);
        if (groupId == null) {
            throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException(
                    "Student is not assigned to any active group for this batch"
            );
        }
        long count = assignmentRepo.countByGroupIdAndBatchIdAndAssignmentStatusAndState(
                groupId, batchId, AssignmentStatus.ASSIGNED, AssignmentState.VERIFIED
        );
        return new CountResponse(count);
    }

    /** Dashboard metric: number of active members in my group. */
    @GetMapping("/stats/group-members/count")
    public CountResponse groupMembersCount() {
        var student = currentUser.student();
        Long studentId = student.getId();
        Long batchId = student.getBatchId();
        Long groupId = externalService.getGroupIdForStudent(batchId, studentId);
        if (groupId == null) {
            throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException(
                    "Student is not assigned to any active group for this batch"
            );
        }
        int count = externalService.getStudentIdsForGroup(groupId).size();
        return new CountResponse(count);
    }

@GetMapping("/assignments/{assignmentId}")
public TaskAssignmentDetailResponse assignmentDetail(
        @PathVariable Long assignmentId
) {
    var student = currentUser.student();
    Long studentId = student.getId();
    Long batchId = student.getBatchId();
    Long groupId = externalService.getGroupIdForStudent(batchId, studentId);
    if (groupId == null) {
        throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException("Student is not assigned to any active group for this batch");
    }

    TaskAssignment a = assignmentRepo.findById(assignmentId)
            .orElseThrow(() -> new com.projexflow.tms.ProjexFlow_TMS.exception.NotFoundException("Assignment not found"));

    if (!batchId.equals(a.getBatchId()) || !groupId.equals(a.getGroupId())) {
        throw new com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException("You do not have access to this assignment");
    }

    var mentorProfile = umsClient.mentorsByIds(List.of(a.getMentorId()))
            .stream()
            .findFirst()
            .orElse(null);

    TaskAssignmentResponse assignment = new TaskAssignmentResponse(
            a.getId(),
            a.getTask().getId(),
            a.getTask().getTitle(),
            a.getBatchId(),
            a.getMentorId(),
            mentorProfile,
            a.getGroupId(),
            a.getAssignedAt(),
            a.getDueAt(),
            a.getAssignmentStatus(),
            a.getState(),
            a.getUpdatedAt()
    );

    List<TaskSubmission> subs = taskService.getSubmissionsForStudent(studentId, assignmentId);
    TaskSubmission latest = subs.stream()
            .max(java.util.Comparator.comparing(TaskSubmission::getSubmittedAt))
            .orElse(null);

    var me = umsClient.studentsByIds(List.of(studentId)).stream().findFirst().orElse(null);
    TaskSubmissionResponse latestResp = latest == null ? null : new TaskSubmissionResponse(
            latest.getId(),
            latest.getAssignment().getId(),
            latest.getBatchId(),
            latest.getGroupId(),
            latest.getSubmittedByStudentId(),
            me,
            latest.getSubmittedAt(),
            latest.getRepoUrl(),
            latest.getPrUrl()
    );

    return new TaskAssignmentDetailResponse(assignment, latestResp);
}
@PostMapping("/assignments/{assignmentId}/submit")
    public IdResponse submit(
            @PathVariable Long assignmentId,
            @Valid @RequestBody SubmitTaskRequest req
    ) {
        Long studentId = currentUser.student().getId();
        Long submissionId = taskService.submitTask(studentId, assignmentId, req);
        return new IdResponse(submissionId);
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public List<TaskSubmissionResponse> mySubmissions(
            @PathVariable Long assignmentId
    ) {
        Long studentId = currentUser.student().getId();

        List<TaskSubmission> subs = taskService.getSubmissionsForStudent(studentId, assignmentId);

        // UI often needs student name/photo for cards; enrich from UMS once.
        var me = umsClient.studentsByIds(List.of(studentId)).stream().findFirst().orElse(null);
        return subs.stream().map(s -> new TaskSubmissionResponse(
                s.getId(),
                s.getAssignment().getId(),
                s.getBatchId(),
                s.getGroupId(),
                s.getSubmittedByStudentId(),
                me,
                s.getSubmittedAt(),
                s.getRepoUrl(),
                s.getPrUrl()
        )).toList();
    }
}

