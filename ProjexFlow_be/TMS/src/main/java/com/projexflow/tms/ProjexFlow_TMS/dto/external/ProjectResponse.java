package com.projexflow.tms.ProjexFlow_TMS.dto.external;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record ProjectResponse(
        String id,
        Long batchId,
        Long groupId,
        String title,
        String description,
        String status,
        java.util.List<String> technologyStack,
        LocalDate startDate,
        LocalDate endDate,
        String repoUrl,
        String docsUrl,
        Instant createdAt,
        Instant updatedAt
) {}
