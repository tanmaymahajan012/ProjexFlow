package com.projexflow.UMS.ProjexFlow_UMS.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Admin extends BaseUser {

    @NotBlank(message = "Designation is required")
    @Size(min = 2, max = 100, message = "Designation must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String designation;

    @PrePersist
    public void prePersist() {
        setRole(Role.ADMIN);
    }

    @PreUpdate
    public void preUpdate() {
        setRole(Role.ADMIN);
    }
}
