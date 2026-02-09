package com.projexflow.als.ProjexFlow_ALS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class CreateActivityLogRequest {

    /**
     * Which service emitted this log. Example: "PMS", "GMS", "TMS"
     * Optional (ALS will still accept null).
     */
    private String sourceService;

    @NotBlank
    private String action;          // e.g. "CREATE_PROJECT", "SUBMIT_TASK"

    private String entityType;      // e.g. "PROJECT", "TASK"
    private String entityId;        // service-specific identifier (string to keep it generic)

    private String description;     // free-text message for UI

    private Map<String, Object> metadata; // optional arbitrary extra info
}
