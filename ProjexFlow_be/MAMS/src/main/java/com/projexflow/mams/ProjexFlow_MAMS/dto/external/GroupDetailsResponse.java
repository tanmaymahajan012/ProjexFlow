package com.projexflow.mams.ProjexFlow_MAMS.dto.external;

import java.time.Instant;
import java.util.List;

/**
 * Mirror of GMS GroupDetailsResponse (UI-friendly group details).
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
