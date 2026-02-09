package com.projexflow.tms.ProjexFlow_TMS.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record AssignTaskResultResponse(
        long created,
        long skipped
) {}



