package com.projexflow.mams.ProjexFlow_MAMS.service;

import com.projexflow.mams.ProjexFlow_MAMS.config.FeignClientConfig;
import com.projexflow.mams.ProjexFlow_MAMS.dto.IdOnlyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "PROJEXFLOW-UMS", configuration = FeignClientConfig.class)
public interface UmsClient {

    @GetMapping("/api/v1/internal/mentors/ids")
    List<Long> getAvailableMentorIds(@RequestParam String course);

    /** Resolve mentorId by email (JWT subject). */
    @GetMapping("/api/v1/internal/mentors/by-email")
    IdOnlyResponse mentorByEmail(@RequestParam("email") String email);
}
