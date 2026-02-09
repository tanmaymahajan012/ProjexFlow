package com.projexflow.pms.ProjexFlow_PMS.service;

import com.projexflow.pms.ProjexFlow_PMS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.projexflow.pms.ProjexFlow_PMS.dto.external.MentorForGroupResponse;

@FeignClient(name = "PROJEXFLOW-MAMS", configuration = FeignClientConfig.class)
public interface MamsClient {

    @GetMapping("/mams/internal/mentors/{mentorId}/batches/{batchId}/groups")
    List<Long> getAssignedGroupIds(@PathVariable Long mentorId, @PathVariable Long batchId);

    @GetMapping("/mams/internal/batches/{batchId}/groups/{groupId}/mentor")
    MentorForGroupResponse getMentorForGroup(@PathVariable Long batchId, @PathVariable Long groupId);

}

