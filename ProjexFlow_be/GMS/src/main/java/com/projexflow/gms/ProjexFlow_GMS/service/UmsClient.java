package com.projexflow.gms.ProjexFlow_GMS.service;

import com.projexflow.gms.ProjexFlow_GMS.config.FeignClientConfig;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "PROJEXFLOW-UMS", configuration = FeignClientConfig.class)
public interface UmsClient {

    @PostMapping("/api/v1/internal/students/batch")
    List<UmsStudentResponse> getStudentsByIds(@RequestBody List<Long> studentIds);

    @GetMapping("/api/v1/internal/students/by-email")
    UmsStudentResponse getStudentByEmail(@RequestParam("email") String email);

    /**
     * Fetch all students for a batch (used when closing grouping phase to ensure no student is left out).
     */
    @GetMapping("/api/v1/internal/students/by-batch")
    List<UmsStudentResponse> getStudentsByBatch(@RequestParam("batchId") Long batchId);

    /**
     * Fetch all students for a batch and course.
     */
    @GetMapping("/api/v1/internal/students/by-batch-course")
    List<UmsStudentResponse> getStudentsByBatchAndCourse(
            @RequestParam("batchId") Long batchId,
            @RequestParam("course") String course
    );
}
