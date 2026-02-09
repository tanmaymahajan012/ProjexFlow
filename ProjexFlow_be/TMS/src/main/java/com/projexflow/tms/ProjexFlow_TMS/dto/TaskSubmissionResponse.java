package com.projexflow.tms.ProjexFlow_TMS.dto;

import com.projexflow.tms.ProjexFlow_TMS.dto.external.StudentResponse;

import java.time.LocalDateTime;

/**
 * UI-friendly submission response.
 * Includes student details to avoid UI having to translate studentId -> profile.
 */
public record TaskSubmissionResponse(
        Long id,
        Long assignmentId,
        Long batchId,
        Long groupId,
        Long submittedByStudentId,
        StudentResponse submittedByStudent,
        LocalDateTime submittedAt,
        String repoUrl,
        String prUrl
) {
}
