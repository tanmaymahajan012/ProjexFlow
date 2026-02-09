package com.projexflow.mams.ProjexFlow_MAMS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "mentor_group",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_batch_group", columnNames = {"batch_id", "group_id"})
        },
        indexes = {
                @Index(name = "idx_batch_mentor", columnList = "batch_id, mentor_id")
        }
)
@Getter@Setter@RequiredArgsConstructor
public class MentorGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
}

