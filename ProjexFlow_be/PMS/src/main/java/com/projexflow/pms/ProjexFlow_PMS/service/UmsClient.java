package com.projexflow.pms.ProjexFlow_PMS.service;

import com.projexflow.pms.ProjexFlow_PMS.config.FeignClientConfig;
import com.projexflow.pms.ProjexFlow_PMS.dto.external.MentorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PROJEXFLOW-UMS", configuration = FeignClientConfig.class)
public interface UmsClient {

    @PostMapping("/api/v1/internal/mentors/batch")
    List<MentorResponse> getMentorsByIds(@RequestBody List<Long> mentorIds);
}
