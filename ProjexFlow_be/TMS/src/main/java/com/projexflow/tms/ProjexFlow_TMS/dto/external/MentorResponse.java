package com.projexflow.tms.ProjexFlow_TMS.dto.external;

import java.time.OffsetDateTime;

/**
 * Mirror of UMS MentorResponse used for inter-service communication.
 */
public record MentorResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String profilePhotoUrl,
        boolean active,
        String course,
        String empId,
        String department,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
