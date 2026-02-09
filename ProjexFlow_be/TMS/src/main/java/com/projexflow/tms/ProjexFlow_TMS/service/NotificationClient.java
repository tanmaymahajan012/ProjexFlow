package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import com.projexflow.tms.ProjexFlow_TMS.dto.external.NotificationCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ProjexFlow-NMS", configuration = FeignClientConfig.class)
public interface NotificationClient {

    @PostMapping("/internal/notifications")
    Map<String, Object> create(@RequestBody NotificationCreateRequest request);
}
