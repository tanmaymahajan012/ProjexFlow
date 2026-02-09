package com.projexflow.tms.ProjexFlow_TMS.dto;

/**
 * UI-friendly detail payload for the assignment page.
 * - assignment: the assignment itself (includes mentor profile)
 * - latestSubmission: latest submission for the authenticated student (may be null)
 */
public record TaskAssignmentDetailResponse(
        TaskAssignmentResponse assignment,
        TaskSubmissionResponse latestSubmission
) {}
