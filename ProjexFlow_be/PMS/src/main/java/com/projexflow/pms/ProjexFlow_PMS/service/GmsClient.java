package com.projexflow.pms.ProjexFlow_PMS.service;

import com.projexflow.pms.ProjexFlow_PMS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.projexflow.pms.ProjexFlow_PMS.dto.external.GroupDetailsResponse;

@FeignClient(name = "PROJEXFLOW-GMS", configuration = FeignClientConfig.class)
public interface GmsClient {

    @GetMapping("/gms/internal/groups/{groupId}/members/{studentId}")
    Map<String, Object> isMemberRaw(@PathVariable Long groupId, @PathVariable Long studentId);

    @GetMapping("/gms/internal/batches/{batchId}/students/{studentId}/group-id")
    Map<String, Object> myGroupIdRaw(@PathVariable Long batchId, @PathVariable Long studentId);

    @GetMapping("/gms/internal/batches/{batchId}/grouping-status")
    Map<String, Object> groupingStatusRaw(@PathVariable Long batchId);

    
    @GetMapping("/gms/groups/{groupId}")
    GroupDetailsResponse getGroupDetails(@PathVariable Long groupId);


    default Boolean isMember(Long batchId, Long groupId, Long studentId) {
        Object v = isMemberRaw(groupId, studentId).get("member");
        return (v instanceof Boolean b) ? b : Boolean.parseBoolean(String.valueOf(v));
    }

    default Long getMyGroupId(Long batchId, Long studentId) {
        Object v = myGroupIdRaw(batchId, studentId).get("groupId");
        if (v == null) return null;
        return Long.valueOf(String.valueOf(v));
    }

    default String groupingStatus(Long batchId) {
        Object v = groupingStatusRaw(batchId).get("status");
        return v == null ? "OPEN" : String.valueOf(v);
    }
}
