package com.projexflow.pms.ProjexFlow_PMS.dto.external;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MentorForGroupResponse {
    private Long mentorId;
    private LocalDateTime assignedAt;
}
