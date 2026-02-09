package com.projexflow.tms.ProjexFlow_TMS.dto;



import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignTaskRequest(
        @NotNull Long batchId,
        @NotEmpty List<Long> groupIds,
        LocalDateTime dueAt
) {}


