package com.projexflow.UMS.ProjexFlow_UMS.entity;

import jakarta.persistence.*;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "mentors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Mentor extends BaseUser {

    @NotBlank(message = "Course is required")
    @Size(min = 2, max = 80, message = "Course must be between 2 and 80 characters")
    @Column(nullable = false, length = 80)
    private String course;

    @NotBlank(message = "Employee ID is required")
    @Size(min = 2, max = 50, message = "Employee ID must be between 2 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String empId;

    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 80, message = "Department must be between 2 and 80 characters")
    @Column(nullable = false, length = 80)
    private String department;

    @PrePersist
    public void prePersist() {
        setRole(Role.MENTOR);
    }

    @PreUpdate
    public void preUpdate() {
        setRole(Role.MENTOR);
    }
}

