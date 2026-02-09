package com.projexflow.als.ProjexFlow_ALS.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {
    /** Stable machine-readable error code */
    private String code;
    /** UI-friendly message that can be shown directly */
    private String uiMessage;
private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
