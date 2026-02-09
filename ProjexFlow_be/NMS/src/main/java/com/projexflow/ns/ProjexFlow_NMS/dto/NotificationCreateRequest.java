package com.projexflow.ns.ProjexFlow_NMS.dto;

import java.util.Map;

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
