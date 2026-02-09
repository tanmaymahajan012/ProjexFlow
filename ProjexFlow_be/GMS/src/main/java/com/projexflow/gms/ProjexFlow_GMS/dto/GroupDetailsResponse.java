package com.projexflow.gms.ProjexFlow_GMS.dto;

import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;

import java.time.Instant;
import java.util.List;

/**
 * UI-friendly group details response: includes group metadata + full member profiles.
 */
public record GroupDetailsResponse(
        Long groupId,
        Long batchId,
        String course,
        Instant createdAt,
        List<MemberDto> members
) {
    public record MemberDto(
            Long memberId,
            Long studentId,
            boolean active,
            Instant joinedAt,
            Instant leftAt,
            UmsStudentResponse student
    ) {}
}
