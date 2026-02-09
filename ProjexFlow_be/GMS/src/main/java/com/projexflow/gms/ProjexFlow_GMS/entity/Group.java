package com.projexflow.gms.ProjexFlow_GMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "student_groups", indexes = {
        @Index(name="idx_groups_batch_course", columnList = "batchId,course")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long batchId;

    @Column(nullable=false, length = 100)
    private String course;

    @Column(nullable=false)
    private Instant createdAt;
}

