package com.projexflow.gms.ProjexFlow_GMS.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Mirror DTO for UMS StudentResponse (used for UI-friendly group member data).
 */
@Getter @Setter
public class UmsStudentResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role; // string to avoid coupling enums across services
    private String profilePhotoUrl;
    private boolean active;
    private String rollNo;
    private String prn;
    private String githubUrl;
    private String course;
    private Long batchId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
