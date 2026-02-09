package com.projexflow.tms.ProjexFlow_TMS.controller;

import com.projexflow.tms.ProjexFlow_TMS.dto.*;
import com.projexflow.tms.ProjexFlow_TMS.entity.*;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskAssignmentRepository;
import com.projexflow.tms.ProjexFlow_TMS.repository.TaskTemplateRepository;
import com.projexflow.tms.ProjexFlow_TMS.identity.CurrentUserResolver;
import com.projexflow.tms.ProjexFlow_TMS.service.TaskManagementService;
import com.projexflow.tms.ProjexFlow_TMS.service.UmsClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tms/mentor")
@RequiredArgsConstructor
public class MentorTaskController {

    private final TaskTemplateRepository taskTemplateRepo;
    private final TaskAssignmentRepository assignmentRepo;
    private final TaskManagementService taskService;
    private final UmsClient umsClient;
    private final CurrentUserResolver currentUser;

    // ----------------- Create Task Template -----------------

    @PostMapping("/tasks")
    public TaskTemplateResponse createTask(
            @Valid @RequestBody CreateTaskTemplateRequest req
    ) {
        Long mentorId = currentUser.mentorId();
        TaskTemplate template = TaskTemplate.builder()
                .batchId(req.batchId())
                .mentorId(mentorId)
                .title(req.title())
                .description(req.description())
                .instructions(req.instructions())
                .defaultDueAt(req.defaultDueAt())
                .active(true)
                .build();

        template = taskTemplateRepo.save(template);

        return new TaskTemplateResponse(
                template.getId(),
                template.getBatchId(),
                template.getMentorId(),
                template.getTitle(),
                template.getDescription(),
                template.getInstructions(),
                template.getDefaultDueAt(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }

    
@GetMapping("/tasks")
public List<TaskTemplateResponse> listMyTasks(
        @RequestParam Long batchId,
        @RequestParam(required = false) Boolean active
) {
    Long mentorId = currentUser.mentorId();
    List<TaskTemplate> tasks = taskTemplateRepo.findAllByMentorIdAndBatchIdOrderByCreatedAtDesc(mentorId, batchId);
    if (active != null) {
        tasks = tasks.stream().filter(t -> t.isActive() == active).toList();
    }
    return tasks.stream().map(t -> new TaskTemplateResponse(
            t.getId(),
            t.getBatchId(),
            t.getMentorId(),
            t.getTitle(),
            t.getDescription(),
            t.getInstructions(),
            t.getDefaultDueAt(),
            t.isActive(),
            t.getCreatedAt(),
            t.getUpdatedAt()
    )).toList();
}

@PatchMapping("/tasks/{taskId}/active")
public TaskTemplateResponse setActive(
        @PathVariable Long taskId,
        @RequestParam boolean value
) {
    Long mentorId = currentUser.mentorId();
    TaskTemplate t = taskTemplateRepo.findByIdAndMentorId(taskId, mentorId)
            .orElseThrow(() -> new com.projexflow.tms.ProjexFlow_TMS.exception.NotFoundException("Task template not found"));
    t.setActive(value);
    t = taskTemplateRepo.save(t);

    return new TaskTemplateResponse(
            t.getId(),
            t.getBatchId(),
            t.getMentorId(),
            t.getTitle(),
            t.getDescription(),
            t.getInstructions(),
            t.getDefaultDueAt(),
            t.isActive(),
            t.getCreatedAt(),
            t.getUpdatedAt()
    );
}

// ----------------- Assign to specific group(s) -----------------
    // ✅ Controller no longer calls any placeholder; service fetches allowed groups via ExternalIntegrationService.

    @PostMapping("/tasks/{taskId}/assign")
    public AssignTaskResultResponse assignToGroups(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest req
    ) {
        Long mentorId = currentUser.mentorId();
        return taskService.assignToGroups(mentorId, taskId, req);
    }

    // ----------------- Assign to ALL groups under mentor -----------------
    // ✅ Assign-all implemented by calling service with groupIds fetched internally via ExternalIntegrationService.
    // We just pass an empty groupIds list here and let service handle it? No.
    // Instead we build AssignTaskRequest with groupIds fetched by service? To keep controller clean,
    // we'll create a dedicated service method for assignAll OR we can reuse assignToGroups by passing
    // groupIds = groupsUnderMentor. We'll add a thin wrapper in controller by calling a new service method.
    //
    // Since you asked "controllers call service only", below is the cleanest: service has assignToAllGroups.

    @PostMapping("/tasks/{taskId}/assign-all")
    public AssignTaskResultResponse assignToAllGroups(
            @PathVariable Long taskId,
            @RequestParam Long batchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueAt
    ) {
        Long mentorId = currentUser.mentorId();
        // NOTE: This requires you to add assignToAllGroups(...) in service.
        return taskService.assignToAllGroups(mentorId, taskId, batchId, dueAt);
    }

    // ----------------- Mentor dashboard -----------------

    @GetMapping("/assignments")
    public List<TaskAssignmentResponse> listAssignments(
            @RequestParam Long batchId,
            @RequestParam(required = false) AssignmentState state
    ) {
        Long mentorId = currentUser.mentorId();

        var mentorProfile = umsClient.mentorsByIds(List.of(mentorId))
                .stream()
                .findFirst()
                .orElse(null);
        List<TaskAssignment> assignments;

        if (state == null) {
            assignments = assignmentRepo.findAllByMentorIdAndBatchIdAndAssignmentStatusOrderByUpdatedAtDesc(
                    mentorId, batchId, AssignmentStatus.ASSIGNED
            );
        } else {
            assignments = assignmentRepo.findAllByMentorIdAndBatchIdAndStateAndAssignmentStatusOrderByUpdatedAtDesc(
                    mentorId, batchId, state, AssignmentStatus.ASSIGNED
            );
        }

        return assignments.stream().map(a -> new TaskAssignmentResponse(
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
        )).toList();
    }

    // ----------------- View submissions for an assignment -----------------

    @GetMapping("/assignments/{assignmentId}/submissions")
    public List<TaskSubmissionResponse> getSubmissions(
            @PathVariable Long assignmentId
    ) {
        Long mentorId = currentUser.mentorId();
        List<TaskSubmission> subs = taskService.getSubmissionsForMentor(mentorId, assignmentId);

        // Enrich students for UI (names/photos for review tables)
        List<Long> studentIds = subs.stream()
                .map(TaskSubmission::getSubmittedByStudentId)
                .distinct()
                .toList();
        java.util.Map<Long, com.projexflow.tms.ProjexFlow_TMS.dto.external.StudentResponse> studentsById = umsClient
                .studentsByIds(studentIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.projexflow.tms.ProjexFlow_TMS.dto.external.StudentResponse::id,
                        java.util.function.Function.identity()
                ));

        return subs.stream().map(s -> new TaskSubmissionResponse(
                s.getId(),
                s.getAssignment().getId(),
                s.getBatchId(),
                s.getGroupId(),
                s.getSubmittedByStudentId(),
                studentsById.get(s.getSubmittedByStudentId()),
                s.getSubmittedAt(),
                s.getRepoUrl(),
                s.getPrUrl()
        )).toList();
    }

    // ----------------- Review latest submission -----------------

    @PostMapping("/assignments/{assignmentId}/review")
    public IdResponse reviewAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody ReviewSubmissionRequest req
    ) {
        Long mentorId = currentUser.mentorId();
        Long reviewId = taskService.reviewAssignment(mentorId, assignmentId, req);
        return new IdResponse(reviewId);
    }
}

