package com.projexflow.mams.ProjexFlow_MAMS.dto;

import com.projexflow.mams.ProjexFlow_MAMS.dto.external.GroupDetailsResponse;

import java.time.LocalDateTime;

/**
 * UI-friendly response for mentor-assigned groups.
 * Includes full group details (members) + the assignment timestamp.
 */
public record AssignedGroupDetailsResponse(
        Long mentorId,
        Long batchId,
        Long groupId,
        LocalDateTime assignedAt,
        GroupDetailsResponse group
) {}
