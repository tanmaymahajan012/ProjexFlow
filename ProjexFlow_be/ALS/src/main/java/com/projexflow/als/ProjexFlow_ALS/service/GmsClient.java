package com.projexflow.als.ProjexFlow_ALS.service;

import com.projexflow.als.ProjexFlow_ALS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "PROJEXFLOW-GMS", configuration = FeignClientConfig.class)
public interface GmsClient {

    @GetMapping("/gms/internal/batches/{batchId}/grouping-status")
    Map<String, Object> groupingStatusRaw(@PathVariable Long batchId);

    default String groupingStatus(Long batchId) {
        Object v = groupingStatusRaw(batchId).get("status");
        return v == null ? "OPEN" : String.valueOf(v);
    }
}
