package com.projexflow.gms.ProjexFlow_GMS.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "group_join_requests",
        indexes = {
                @Index(name="idx_req_batch_course_to", columnList="batchId,course,toStudentId,status"),
                @Index(name="idx_req_batch_course_from", columnList="batchId,course,fromStudentId,status")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GroupJoinRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long batchId;

    @Column(nullable=false, length = 100)
    private String course;

    @Column(nullable=false)
    private Long fromStudentId;

    @Column(nullable=false)
    private Long toStudentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private RequestStatus status;

    @Column(nullable=false)
    private Instant createdAt;

    private Instant respondedAt;
}
