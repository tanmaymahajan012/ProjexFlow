package com.projexflow.gms.ProjexFlow_GMS.dto;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
