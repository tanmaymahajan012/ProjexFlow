package com.projexflow.tms.ProjexFlow_TMS.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTaskTemplateRequest(
        @NotNull Long batchId,
        @NotBlank String title,
        @NotBlank String description,
        String instructions,
        LocalDateTime defaultDueAt
) {}

