package com.projexflow.gms.ProjexFlow_GMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentGroupRequestResponse {
    private Long id;
    private Long toStudentId;
    private String toStudentEmail;
    private String toStudentName;
    private String status;
    private Instant createdAt;
    private Instant respondedAt;
}
