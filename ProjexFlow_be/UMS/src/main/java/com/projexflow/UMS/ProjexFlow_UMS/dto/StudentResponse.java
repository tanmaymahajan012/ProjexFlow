package com.projexflow.UMS.ProjexFlow_UMS.dto;

import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role; // STUDENT
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

