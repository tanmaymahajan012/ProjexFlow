package com.projexflow.tms.ProjexFlow_TMS.dto.external;

import lombok.Builder;

import java.util.Map;

/**
 * DTO used by TMS to emit activity logs to ALS.
 * Mirrors ALS CreateActivityLogRequest.
 */
@Builder
public record CreateActivityLogRequest(
        String sourceService,
        String action,
        String entityType,
        String entityId,
        String description,
        Map<String, Object> metadata
) {}
