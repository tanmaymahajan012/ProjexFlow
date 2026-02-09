package com.projexflow.gms.ProjexFlow_GMS.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendRequestDto {
    @NotNull private Long batchId;
    private String course;
    @NotNull private Long toStudentId;
}

