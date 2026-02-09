package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.IdOnlyResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorResponse;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
import com.projexflow.UMS.ProjexFlow_UMS.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class InternalMentorQueryController {

    private final MentorRepository mentorRepository;

    /**
     * Batch fetch mentors by IDs for UI enrichment in downstream services (TMS/PMS/etc).
     */
    @PostMapping("/mentors/batch")
    public List<MentorResponse> mentorsByIds(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return mentorRepository.findAllById(ids)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Used by MAMS during mentor assignment.
     * Returns IDs of active mentors matching the given course (case-insensitive).
     */
    @GetMapping("/mentors/ids")
    public List<Long> mentorIdsByCourse(@RequestParam String course) {
        return mentorRepository.findAllByCourseIgnoreCaseAndActiveTrue(course)
                .stream()
                .map(m -> m.getId())
                .toList();
    }

    /**
     * Resolve mentorId by email (used by downstream services to map JWT subject -> role-specific ID).
     */
    @GetMapping("/mentors/by-email")
    public IdOnlyResponse mentorByEmail(@RequestParam("email") String email) {
        Long id = mentorRepository.findByEmail(email)
                .map(m -> m.getId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Mentor not found: " + email
                ));
        return new IdOnlyResponse(id);
    }

    private MentorResponse toResponse(Mentor m) {
        return new MentorResponse(
                m.getId(),
                m.getFullName(),
                m.getEmail(),
                m.getRole(),
                m.getProfilePhotoUrl(),
                m.isActive(),
                m.getCourse(),
                m.getEmpId(),
                m.getDepartment(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }

}
