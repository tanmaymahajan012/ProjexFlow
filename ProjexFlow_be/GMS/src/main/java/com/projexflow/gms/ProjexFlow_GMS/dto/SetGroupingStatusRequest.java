package com.projexflow.gms.ProjexFlow_GMS.dto;

import com.projexflow.gms.ProjexFlow_GMS.entity.GroupingPhase;
import jakarta.validation.constraints.NotNull;

public record SetGroupingStatusRequest(@NotNull GroupingPhase status) {
}
