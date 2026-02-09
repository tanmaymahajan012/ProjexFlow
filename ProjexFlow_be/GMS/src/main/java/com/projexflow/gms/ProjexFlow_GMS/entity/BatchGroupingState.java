package com.projexflow.gms.ProjexFlow_GMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "batch_grouping_state",
        uniqueConstraints = @UniqueConstraint(name = "uq_batch_course", columnNames = {"batchId","course"}),
        indexes = @Index(name="idx_grouping_batch_course", columnList = "batchId,course")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class BatchGroupingState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long batchId;

    @Column(nullable = false, length = 100)
    private String course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupingPhase phase;

    @Column(nullable = false)
    private Instant updatedAt;
}
