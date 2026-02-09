package com.projexflow.gms.ProjexFlow_GMS.dto;

/**
 * UI helper response: returns the current student's active group in a batch (if any),
 * along with full group details useful for UI rendering.
 */
public record MyGroupResponse(
        Long groupId,
        GroupDetailsResponse group
) {}
