package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.CountResponse;
import com.projexflow.UMS.ProjexFlow_UMS.repository.MentorRepository;
import com.projexflow.UMS.ProjexFlow_UMS.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin dashboard metrics.
 *
 * Note: Auth is typically enforced at the API Gateway; this service keeps endpoints simple.
 */
@RestController
@RequestMapping("/api/v1/admins/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;

    /** Total number of student records (active + inactive). */
    @GetMapping("/students/total-count")
    public CountResponse totalStudents() {
        return new CountResponse(studentRepository.count());
    }

    /** Number of active students. */
    @GetMapping("/students/active-count")
    public CountResponse activeStudents() {
        return new CountResponse(studentRepository.countByActiveTrue());
    }

    /** Number of active mentors. */
    @GetMapping("/mentors/active-count")
    public CountResponse activeMentors() {
        return new CountResponse(mentorRepository.countByActiveTrue());
    }
}
