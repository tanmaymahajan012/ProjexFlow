package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "PROJEXFLOW-MAMS", configuration = FeignClientConfig.class)
public interface MamsClient {

    @GetMapping("/mams/internal/mentors/{mentorId}/batches/{batchId}/groups")
    List<Long> getAssignedGroupIds(@PathVariable Long mentorId, @PathVariable Long batchId);
}
