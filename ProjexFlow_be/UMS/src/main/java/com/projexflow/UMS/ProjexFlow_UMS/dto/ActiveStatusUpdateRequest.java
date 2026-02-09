package com.projexflow.UMS.ProjexFlow_UMS.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveStatusUpdateRequest {
    @NotNull
    private Boolean active;
}
