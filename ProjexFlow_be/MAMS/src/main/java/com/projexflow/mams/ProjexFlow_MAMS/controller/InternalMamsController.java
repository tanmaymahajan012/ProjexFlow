package com.projexflow.mams.ProjexFlow_MAMS.controller;

import com.projexflow.mams.ProjexFlow_MAMS.service.MentorAssignmentService;
import com.projexflow.mams.ProjexFlow_MAMS.dto.MentorForGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal-only endpoints used for service-to-service communication.
 * Keep responses minimal and stable for other services (IDs etc.).
 */
@RestController
@RequestMapping("/mams/internal")
@RequiredArgsConstructor
public class InternalMamsController {

    private final MentorAssignmentService service;

    /**
     * Return assigned group IDs for a mentor within a batch.
     * Used by TMS/PMS for authorization and assignment fan-out.
     */
    @GetMapping("/mentors/{mentorId}/batches/{batchId}/groups")
    public List<Long> groupIdsForMentorInBatch(
            @PathVariable Long mentorId,
            @PathVariable Long batchId
    ) {
        return service.getAssignedGroupIds(mentorId, batchId);
    }

    /**
     * Return mentor assignment for a specific group in a batch (if assigned).
     * Useful for enriching UI responses in other services (e.g., PMS project cards).
     */
    @GetMapping("/batches/{batchId}/groups/{groupId}/mentor")
    public MentorForGroupResponse mentorForGroup(
            @PathVariable Long batchId,
            @PathVariable Long groupId
    ) {
        return service.getMentorForGroup(batchId, groupId);
    }

}
