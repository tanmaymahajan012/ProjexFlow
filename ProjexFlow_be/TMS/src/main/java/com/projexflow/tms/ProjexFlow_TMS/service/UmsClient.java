package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.MentorResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.StudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PROJEXFLOW-UMS", configuration = FeignClientConfig.class)
public interface UmsClient {

    @PostMapping("/api/v1/internal/students/batch")
    List<StudentResponse> studentsByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/internal/mentors/batch")
    List<MentorResponse> mentorsByIds(@RequestBody List<Long> ids);
}
