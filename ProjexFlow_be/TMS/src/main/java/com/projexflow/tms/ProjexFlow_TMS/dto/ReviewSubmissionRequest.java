package com.projexflow.tms.ProjexFlow_TMS.dto;


import com.projexflow.tms.ProjexFlow_TMS.entity.ReviewDecision;
import com.projexflow.tms.ProjexFlow_TMS.entity.ReviewReasonCode;
import jakarta.validation.constraints.NotNull;

public record ReviewSubmissionRequest(
        @NotNull ReviewDecision decision,
        ReviewReasonCode reasonCode,
        String comments
) {}
