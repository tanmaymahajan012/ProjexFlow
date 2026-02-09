package com.projexflow.gms.ProjexFlow_GMS.service;

import com.projexflow.gms.ProjexFlow_GMS.config.FeignClientConfig;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.NotificationCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ProjexFlow-NMS", configuration = FeignClientConfig.class)
public interface NotificationClient {

    @PostMapping("/internal/notifications")
    Map<String, Object> create(@RequestBody NotificationCreateRequest request);
}
