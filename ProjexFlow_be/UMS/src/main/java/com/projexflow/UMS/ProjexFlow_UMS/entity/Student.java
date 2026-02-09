package com.projexflow.UMS.ProjexFlow_UMS.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Student extends BaseUser {

    @NotBlank(message = "Roll number is required")
    @Size(min = 1, max = 30, message = "Roll number must be between 1 and 30 characters")
    @Column(nullable = false, length = 30)
    private String rollNo;

    @NotBlank(message = "PRN is required")
    @Size(min = 5, max = 50, message = "PRN must be between 5 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String prn;

    @Pattern(
            regexp = "^(https?://).*$",
            message = "GitHub URL must be a valid http/https URL"
    )
    @Column(length = 300)
    private String githubUrl;

    @NotBlank(message = "Course is required")
    @Size(min = 2, max = 80, message = "Course must be between 2 and 80 characters")
    @Column(nullable = false, length = 80)
    private String course;

    @NotNull(message = "Batch ID is required")
    @Column(nullable = false)
    private Long batchId;

    @PrePersist
    public void prePersist() {
        setRole(Role.STUDENT);
    }

    @PreUpdate
    public void preUpdate() {
        setRole(Role.STUDENT);
    }
}
