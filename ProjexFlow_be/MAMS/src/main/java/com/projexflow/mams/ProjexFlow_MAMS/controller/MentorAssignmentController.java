package com.projexflow.mams.ProjexFlow_MAMS.controller;

import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignRequest;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignResponse;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignedGroupDetailsResponse;
import com.projexflow.mams.ProjexFlow_MAMS.dto.CountResponse;
import com.projexflow.mams.ProjexFlow_MAMS.service.UmsClient;
import com.projexflow.mams.ProjexFlow_MAMS.service.MentorAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/mams")
public class MentorAssignmentController {

    private final MentorAssignmentService service;
    private final UmsClient umsClient;

    public MentorAssignmentController(MentorAssignmentService service, UmsClient umsClient) {
        this.service = service;
        this.umsClient = umsClient;
    }

    // 1) Wipe and reassign all groups
    @PostMapping("/assign")
    public ResponseEntity<AssignResponse> assign(@RequestBody AssignRequest request) {
        return ResponseEntity.ok(service.wipeAndAssign(request));
    }

    // 2) Get assigned groups for a mentor (in a batch)
    @GetMapping("/mentors/{mentorId}/batches/{batchId}/groups")
    public ResponseEntity<List<Long>> getGroups(@PathVariable Long mentorId, @PathVariable Long batchId) {
        return ResponseEntity.ok(service.getAssignedGroupIds(mentorId, batchId));
    }

    /**
     * UI-friendly: mentor self view. MentorId is derived from JWT subject (email) via UMS internal query.
     */
    @GetMapping("/mentor/batches/{batchId}/groups")
    public ResponseEntity<List<AssignedGroupDetailsResponse>> myAssignedGroups(
            @PathVariable Long batchId,
            Principal principal
    ) {
        String email = principal == null ? null : principal.getName();
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long mentorId = umsClient.mentorByEmail(email).id();
        return ResponseEntity.ok(service.getAssignedGroupsDetailed(mentorId, batchId));
    }

    /** Dashboard metric: mentor self view - how many groups are assigned to me for a batch. */
    @GetMapping("/mentor/batches/{batchId}/groups/count")
    public ResponseEntity<CountResponse> myAssignedGroupsCount(
            @PathVariable Long batchId,
            Principal principal
    ) {
        String email = principal == null ? null : principal.getName();
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Long mentorId = umsClient.mentorByEmail(email).id();
        long count = service.countAssignedGroups(mentorId, batchId);
        return ResponseEntity.ok(new CountResponse(count));
    }

    /**
     * UI-friendly: admin view of a mentor's assigned groups with full group details.
     */
    @GetMapping("/mentors/{mentorId}/batches/{batchId}/groups/details")
    public ResponseEntity<List<AssignedGroupDetailsResponse>> mentorGroupsDetailed(
            @PathVariable Long mentorId,
            @PathVariable Long batchId
    ) {
        return ResponseEntity.ok(service.getAssignedGroupsDetailed(mentorId, batchId));
    }
}

