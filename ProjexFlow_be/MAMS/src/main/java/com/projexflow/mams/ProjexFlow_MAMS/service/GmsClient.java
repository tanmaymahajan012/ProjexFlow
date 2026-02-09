package com.projexflow.mams.ProjexFlow_MAMS.service;

import com.projexflow.mams.ProjexFlow_MAMS.config.FeignClientConfig;
import com.projexflow.mams.ProjexFlow_MAMS.dto.external.GroupDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "PROJEXFLOW-GMS", configuration = FeignClientConfig.class)
public interface GmsClient {

    @GetMapping("/gms/internal/batches/{batchId}/groups")
    List<Long> getFinalGroupIds(@PathVariable Long batchId);

    /** UI-friendly: fetch full group details (members) by groupId. */
    @GetMapping("/gms/groups/{groupId}")
    GroupDetailsResponse getGroupDetails(@PathVariable Long groupId);


@GetMapping("/gms/internal/batches/{batchId}/grouping-status")
Map<String, Object> groupingStatusRaw(@PathVariable Long batchId);

default String groupingStatus(Long batchId) {
    Object v = groupingStatusRaw(batchId).get("status");
    return v == null ? "OPEN" : String.valueOf(v);
}

}
