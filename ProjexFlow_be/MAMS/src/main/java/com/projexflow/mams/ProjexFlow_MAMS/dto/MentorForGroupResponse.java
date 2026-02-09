package com.projexflow.mams.ProjexFlow_MAMS.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MentorForGroupResponse {
    private Long mentorId;
    private LocalDateTime assignedAt;
}
