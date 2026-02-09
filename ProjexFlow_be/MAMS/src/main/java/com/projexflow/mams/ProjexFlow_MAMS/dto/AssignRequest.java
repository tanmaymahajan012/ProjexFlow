package com.projexflow.mams.ProjexFlow_MAMS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AssignRequest {

    private Long batchId;
    private String course;
}

