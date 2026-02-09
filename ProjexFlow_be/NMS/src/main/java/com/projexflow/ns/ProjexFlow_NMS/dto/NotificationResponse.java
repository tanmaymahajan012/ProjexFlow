package com.projexflow.ns.ProjexFlow_NMS.dto;

import java.time.Instant;
import java.util.Map;

public record NotificationResponse(
        Long id,
        Long recipientId,
        String recipientRole,
        String type,
        String title,
        String message,
        String referenceType,
        Long referenceId,
        Map<String, Object> metadata,
        boolean read,
        Instant createdAt,
        Instant readAt
) {}
