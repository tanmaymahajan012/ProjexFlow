package com.projexflow.UMS.ProjexFlow_UMS.dto;


import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType; // "Bearer"
    private Long userId;      // domain user id
    private Role role;
    private String email;
}

