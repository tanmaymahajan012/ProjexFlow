package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.CreateActivityLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for ALS (Activity Log Service).
 *
 * NOTE: FeignClientConfig forwards Authorization header from the incoming request,
 * so ALS can record the real actor (student/mentor).
 */
@FeignClient(name = "PROJEXFLOW-ALS", configuration = FeignClientConfig.class)
public interface ActivityLogClient {

    @PostMapping("/als/logs")
    Object create(@RequestBody CreateActivityLogRequest request);
}
