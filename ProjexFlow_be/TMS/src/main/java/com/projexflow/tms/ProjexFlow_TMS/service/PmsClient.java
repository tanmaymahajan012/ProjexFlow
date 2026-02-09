package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.ProjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PROJEXFLOW-PMS", configuration = FeignClientConfig.class)
public interface PmsClient {

    @GetMapping("/pms/projects")
    ProjectResponse getByBatchAndGroup(@RequestParam Long batchId, @RequestParam Long groupId);
}
