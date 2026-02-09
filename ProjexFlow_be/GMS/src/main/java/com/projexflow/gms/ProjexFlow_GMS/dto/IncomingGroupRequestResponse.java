package com.projexflow.gms.ProjexFlow_GMS.dto;

import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;
import com.projexflow.gms.ProjexFlow_GMS.entity.RequestStatus;
import lombok.*;

import java.time.Instant;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class IncomingGroupRequestResponse {
    private Long requestId;
    private Long batchId;
    private String course;
    private RequestStatus status;
    private Instant createdAt;

    /**
     * UI: show buttons only if actionable=true
     */
    private boolean actionable;

    /**
     * UI widget needs sender details (photo, name, prn, email)
     */
    private UmsStudentResponse sender;
}

