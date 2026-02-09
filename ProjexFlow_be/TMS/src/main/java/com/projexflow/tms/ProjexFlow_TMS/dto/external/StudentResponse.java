package com.projexflow.tms.ProjexFlow_TMS.dto.external;

import java.time.OffsetDateTime;

/**
 * Mirror of UMS StudentResponse used for inter-service communication.
 */
public record StudentResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String profilePhotoUrl,
        boolean active,
        Long batchId,
        String course,
        String prn,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
