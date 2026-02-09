package com.projexflow.gms.ProjexFlow_GMS.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "group_members",
        uniqueConstraints = @UniqueConstraint(name="uq_group_student", columnNames = {"groupId","studentId"}),
        indexes = {
                @Index(name="idx_members_student", columnList = "studentId"),
                @Index(name="idx_members_group", columnList = "groupId"),
                @Index(name="idx_members_batch_course_active", columnList = "batchId,course,active"),
                @Index(name="idx_members_active", columnList = "active")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable=false)
    private Long groupId;

    @Column(nullable=false)
    private Long batchId;

    @Column(nullable=false, length = 100)
    private String course;

    @Column(nullable=false)
    private Long studentId;

    @Column(nullable=false)
    private boolean active;

    @Column(nullable=false)
    private Instant joinedAt;

    private Instant leftAt;
}
