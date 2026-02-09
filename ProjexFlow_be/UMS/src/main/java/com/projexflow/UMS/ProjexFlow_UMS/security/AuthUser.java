package com.projexflow.UMS.ProjexFlow_UMS.security;


import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "auth_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_auth_role_user", columnNames = {"role", "domainUserId"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login identifier
    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // Links to Admin/Mentor/Student id
    @Column(nullable = false)
    private Long domainUserId;

    @Column(nullable = false)
    private boolean active = true;
}

