package com.projexflow.gms.ProjexFlow_GMS.dto;

import com.projexflow.gms.ProjexFlow_GMS.entity.GroupingPhase;
import lombok.Builder;

@Builder
public record GroupingStatusResponse(GroupingPhase status) {
}
