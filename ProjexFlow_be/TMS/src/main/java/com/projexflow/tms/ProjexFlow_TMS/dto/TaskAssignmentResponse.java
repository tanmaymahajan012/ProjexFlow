package com.projexflow.tms.ProjexFlow_TMS.dto;

import com.projexflow.tms.ProjexFlow_TMS.dto.external.MentorResponse;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentState;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentStatus;

import java.time.LocalDateTime;

/**
 * UI-friendly assignment response.
 *
 * NOTE: we keep mentorId/groupId as primitives for filtering, but also include mentor details
 * so the UI can render names/photos without extra client-side joins.
 */
public record TaskAssignmentResponse(
        Long id,
        Long taskId,
        String taskTitle,
        Long batchId,
        Long mentorId,
        MentorResponse mentor,
        Long groupId,
        LocalDateTime assignedAt,
        LocalDateTime dueAt,
        AssignmentStatus assignmentStatus,
        AssignmentState state,
        LocalDateTime updatedAt
) {
}
