package com.projexflow.pms.ProjexFlow_PMS.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error payload for UI + API clients.
 */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String uiMessage,
        String path,
        Map<String, String> fieldErrors
) {}
