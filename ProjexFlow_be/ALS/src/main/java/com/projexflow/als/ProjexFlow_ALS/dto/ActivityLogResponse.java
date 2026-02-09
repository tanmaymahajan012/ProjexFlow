package com.projexflow.als.ProjexFlow_ALS.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ActivityLogResponse {
    private Long id;

    private String userId;
    private String userEmail;
    private String userRole;

    private String sourceService;
    private String action;
    private String entityType;
    private String entityId;

    private String description;
    private Map<String, Object> metadata;

    private Instant createdAt;
}
