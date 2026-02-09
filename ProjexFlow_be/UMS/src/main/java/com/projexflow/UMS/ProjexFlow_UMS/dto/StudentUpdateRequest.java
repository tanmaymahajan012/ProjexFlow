package com.projexflow.UMS.ProjexFlow_UMS.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StudentUpdateRequest {

    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
    private String fullName;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Pattern(regexp = "^(https?://).*$", message = "Profile photo URL must be a valid http/https URL")
    private String profilePhotoUrl;

    private Boolean active;

    @Size(min = 1, max = 30, message = "Roll number must be between 1 and 30 characters")
    private String rollNo;

    @Size(min = 5, max = 50, message = "PRN must be between 5 and 50 characters")
    private String prn;

    @Pattern(regexp = "^(https?://).*$", message = "GitHub URL must be a valid http/https URL")
    private String githubUrl;

    @Size(min = 2, max = 80, message = "Course must be between 2 and 80 characters")
    private String course;

    @NotNull(message = "Batch ID must not be null")
    private Long batchId;
}

