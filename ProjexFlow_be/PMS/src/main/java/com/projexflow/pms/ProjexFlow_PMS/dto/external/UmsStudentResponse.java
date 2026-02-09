package com.projexflow.pms.ProjexFlow_PMS.dto.external;

/**
 * Mirror of UMS student response (minimal UI fields).
 */
public record UmsStudentResponse(
        Long id,
        String name,
        String email,
        Long batchId,
        String prn,
        String course,
        String profileImageUrl
) {}
