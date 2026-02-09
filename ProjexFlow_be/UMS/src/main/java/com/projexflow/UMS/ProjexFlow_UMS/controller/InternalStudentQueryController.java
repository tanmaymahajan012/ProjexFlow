package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentResponse;
import com.projexflow.UMS.ProjexFlow_UMS.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal endpoints used by other services (GMS/TMS/NS) to fetch UI-ready student data.
 */
@RestController
@RequestMapping("/api/v1/internal/students")
@RequiredArgsConstructor
public class InternalStudentQueryController {

    private final StudentService studentService;

    @PostMapping("/batch")
    public List<StudentResponse> batchByIds(@RequestBody List<Long> ids) {
        return studentService.getByIds(ids);
    }

    /**
     * Resolve a single student by email.
     * Useful when downstream services rely on the JWT subject (email) and should not trust/require any extra IDs.
     */
    @GetMapping("/by-email")
    public StudentResponse byEmail(@RequestParam("email") String email) {
        return studentService.getByEmail(email);
    }

    /**
     * List students by batch (used by GMS when closing grouping phase).
     */
    @GetMapping("/by-batch")
    public List<StudentResponse> byBatch(@RequestParam("batchId") Long batchId) {
        return studentService.getByBatchId(batchId);
    }

    /**
     * List students by batch and course (used by GMS when closing grouping phase).
     */
    @GetMapping("/by-batch-course")
    public List<StudentResponse> byBatchAndCourse(
            @RequestParam("batchId") Long batchId,
            @RequestParam("course") String course
    ) {
        return studentService.getByBatchIdAndCourse(batchId, course);
    }
}
