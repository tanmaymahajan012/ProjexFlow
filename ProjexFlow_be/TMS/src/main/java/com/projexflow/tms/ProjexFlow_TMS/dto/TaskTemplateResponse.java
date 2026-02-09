package com.projexflow.tms.ProjexFlow_TMS.dto;


import java.time.LocalDateTime;

public record TaskTemplateResponse(
        Long id,
        Long batchId,
        Long mentorId,
        String title,
        String description,
        String instructions,
        LocalDateTime defaultDueAt,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

