package com.projexflow.gms.ProjexFlow_GMS.controller;

import com.projexflow.gms.ProjexFlow_GMS.dto.GroupingStatusResponse;
import com.projexflow.gms.ProjexFlow_GMS.dto.SetGroupingStatusRequest;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;
import com.projexflow.gms.ProjexFlow_GMS.entity.BatchGroupingState;
import com.projexflow.gms.ProjexFlow_GMS.entity.GroupMember;
import com.projexflow.gms.ProjexFlow_GMS.entity.Group;
import com.projexflow.gms.ProjexFlow_GMS.entity.GroupingPhase;
import com.projexflow.gms.ProjexFlow_GMS.repository.BatchGroupingStateRepo;
import com.projexflow.gms.ProjexFlow_GMS.repository.GroupMemberRepo;
import com.projexflow.gms.ProjexFlow_GMS.repository.GroupRepo;
import com.projexflow.gms.ProjexFlow_GMS.service.UmsClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/gms/internal")
@RequiredArgsConstructor
public class InternalGmsController {

    private final GroupRepo groupRepo;
    private final GroupMemberRepo memberRepo;
    private final BatchGroupingStateRepo groupingRepo;
    private final UmsClient umsClient;

    /**
     * List all groupIds for a batch (used by MAMS for mentor assignment).
     */
    @GetMapping("/batches/{batchId}/groups")
    public List<Long> listGroupIds(@PathVariable Long batchId,
                                  @RequestParam(name = "course", required = false) String course) {
        return ((course == null || course.isBlank())
                ? groupRepo.findAllByBatchId(batchId)
                : groupRepo.findAllByBatchIdAndCourseIgnoreCase(batchId, course))
                .stream().map(Group::getId).toList();
    }

    /**
     * Active studentIds for a group (used by TMS for validation).
     */
    @GetMapping("/groups/{groupId}/students")
    public List<Long> groupStudentIds(@PathVariable Long groupId) {
        return memberRepo.findAllByGroupIdAndActiveTrue(groupId).stream().map(GroupMember::getStudentId).toList();
    }

    /**
     * Check active membership (used by PMS).
     */
    @GetMapping("/groups/{groupId}/members/{studentId}")
    public Map<String, Object> isMember(@PathVariable Long groupId, @PathVariable Long studentId) {
        boolean ok = memberRepo.existsByGroupIdAndStudentIdAndActiveTrue(groupId, studentId);
        return Map.of("member", ok);
    }

    /**
     * Find student's current active groupId in a batch (used by PMS).
     */
    @GetMapping("/batches/{batchId}/students/{studentId}/group-id")
    public Map<String, Object> myGroupId(@PathVariable Long batchId, @PathVariable Long studentId) {
        Long groupId = memberRepo.findActiveGroupId(batchId, studentId).orElse(null);
        return Map.of("groupId", groupId);
    }

    /**
     * Grouping status per batch (used by PMS to allow project creation only after CLOSED).
     */
    @GetMapping("/batches/{batchId}/grouping-status")
    public GroupingStatusResponse groupingStatus(@PathVariable Long batchId,
                                                 @RequestParam(name = "course", required = false) String course) {
        if (course != null && !course.isBlank()) {
            GroupingPhase phase = groupingRepo.findByBatchIdAndCourseIgnoreCase(batchId, course)
                    .map(BatchGroupingState::getPhase)
                    .orElse(GroupingPhase.OPEN);
            return GroupingStatusResponse.builder().status(phase).build();
        }

        // Backward compatible: if any course is still OPEN -> OPEN; else CLOSED (if there is at least one state).
        List<BatchGroupingState> states = groupingRepo.findAllByBatchId(batchId);
        if (states.isEmpty()) return GroupingStatusResponse.builder().status(GroupingPhase.OPEN).build();

        boolean allClosed = states.stream().allMatch(s -> s.getPhase() == GroupingPhase.CLOSED);
        return GroupingStatusResponse.builder().status(allClosed ? GroupingPhase.CLOSED : GroupingPhase.OPEN).build();
    }

    /**
     * Admin sets grouping status for a batch.
     */
    @PutMapping("/batches/{batchId}/grouping-status")
    @Transactional
    public ResponseEntity<GroupingStatusResponse> setGroupingStatus(
            @PathVariable Long batchId,
            @Valid @RequestBody SetGroupingStatusRequest request,
            @RequestParam(name = "course", required = false) String course
    ) {
        Instant now = Instant.now();

        // Determine which course(s) this operation applies to.
        // - If course is provided: apply only that course.
        // - If course is absent: apply to all courses present among active students in the batch.
        Set<String> coursesToProcess = new HashSet<>();
        if (course != null && !course.isBlank()) {
            coursesToProcess.add(course);
        } else {
            List<UmsStudentResponse> students = umsClient.getStudentsByBatch(batchId);
            for (UmsStudentResponse s : students) {
                if (s != null && s.isActive() && s.getCourse() != null && !s.getCourse().isBlank()) {
                    coursesToProcess.add(s.getCourse());
                }
            }
        }

        // If admin is closing the grouping phase, ensure every active student in each (batch, course)
        // belongs to some group. Any remaining students will be assigned to a newly created single-member group.
        if (request.status() == GroupingPhase.CLOSED) {
            for (String c : coursesToProcess) {
                List<UmsStudentResponse> students = umsClient.getStudentsByBatchAndCourse(batchId, c);

                Set<Long> allActiveStudentIds = new HashSet<>();
                for (UmsStudentResponse s : students) {
                    if (s != null && s.isActive()) {
                        allActiveStudentIds.add(s.getId());
                    }
                }

                // Student IDs already present in any active group in this (batch, course).
                Set<Long> alreadyGrouped = new HashSet<>();
                for (GroupMember m : memberRepo.findAllByBatchIdAndCourseIgnoreCaseAndActiveTrue(batchId, c)) {
                    alreadyGrouped.add(m.getStudentId());
                }

                // Create a single-member group for any active student not yet grouped.
                for (Long studentId : allActiveStudentIds) {
                    if (!alreadyGrouped.contains(studentId)) {
                        Group g = groupRepo.save(Group.builder()
                                .batchId(batchId)
                                .course(c)
                                .createdAt(now)
                                .build());
                        memberRepo.save(GroupMember.builder()
                                .groupId(g.getId())
                                .batchId(batchId)
                                .course(c)
                                .studentId(studentId)
                                .active(true)
                                .joinedAt(now)
                                .build());
                    }
                }
            }
        }

        // Persist phase state per (batch, course)
        for (String c : coursesToProcess) {
            BatchGroupingState state = groupingRepo.findByBatchIdAndCourseIgnoreCase(batchId, c)
                    .orElse(BatchGroupingState.builder().batchId(batchId).course(c).build());
            state.setPhase(request.status());
            state.setUpdatedAt(now);
            groupingRepo.save(state);
        }

        // For backward compatibility, return CLOSED only if all courses are CLOSED; else OPEN.
        GroupingPhase phase;
        if (coursesToProcess.isEmpty()) {
            phase = GroupingPhase.OPEN;
        } else {
            boolean allClosed = groupingRepo.findAllByBatchId(batchId).stream()
                    .allMatch(s -> s.getPhase() == GroupingPhase.CLOSED);
            phase = allClosed ? GroupingPhase.CLOSED : GroupingPhase.OPEN;
        }
        return ResponseEntity.ok(GroupingStatusResponse.builder().status(phase).build());
    }
}
