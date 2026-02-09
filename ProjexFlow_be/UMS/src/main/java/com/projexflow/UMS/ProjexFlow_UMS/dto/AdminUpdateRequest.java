package com.projexflow.UMS.ProjexFlow_UMS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AdminUpdateRequest {

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

    @Size(min = 2, max = 100, message = "Designation must be between 2 and 100 characters")
    private String designation;
}

