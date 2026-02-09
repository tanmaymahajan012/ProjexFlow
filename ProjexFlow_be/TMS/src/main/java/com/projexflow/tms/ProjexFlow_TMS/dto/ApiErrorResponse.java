package com.projexflow.tms.ProjexFlow_TMS.dto;

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
