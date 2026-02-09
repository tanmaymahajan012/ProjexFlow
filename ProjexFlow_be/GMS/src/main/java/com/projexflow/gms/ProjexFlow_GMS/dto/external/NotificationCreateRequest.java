package com.projexflow.gms.ProjexFlow_GMS.dto.external;

import java.util.Map;

/**
 * Request sent to Notification Service.
 */
public record NotificationCreateRequest(
        Long recipientId,
        String recipientRole,
        String type,
        String title,
        String message,
        String referenceType,
        Long referenceId,
        Map<String, Object> metadata
) {}
