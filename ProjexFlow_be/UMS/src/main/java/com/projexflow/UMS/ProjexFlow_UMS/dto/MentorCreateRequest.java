package com.projexflow.UMS.ProjexFlow_UMS.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MentorCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Pattern(regexp = "^(https?://).*$", message = "Profile photo URL must be a valid http/https URL")
    private String profilePhotoUrl;

    private boolean active = true;

    @NotBlank(message = "Course is required")
    @Size(min = 2, max = 80, message = "Course must be between 2 and 80 characters")
    private String course;

    @NotBlank(message = "Employee ID is required")
    @Size(min = 2, max = 50, message = "Employee ID must be between 2 and 50 characters")
    private String empId;

    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 80, message = "Department must be between 2 and 80 characters")
    private String department;
}
