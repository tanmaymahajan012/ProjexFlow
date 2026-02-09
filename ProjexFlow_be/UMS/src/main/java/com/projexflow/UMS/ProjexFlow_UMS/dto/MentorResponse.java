package com.projexflow.UMS.ProjexFlow_UMS.dto;

import java.time.OffsetDateTime;

import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MentorResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role; // MENTOR
    private String profilePhotoUrl;
    private boolean active;
    private String course;
    private String empId;
    private String department;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

