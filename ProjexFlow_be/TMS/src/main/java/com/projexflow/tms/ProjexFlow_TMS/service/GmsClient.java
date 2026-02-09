package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "PROJEXFLOW-GMS", configuration = FeignClientConfig.class)
public interface GmsClient {

    @GetMapping("/gms/internal/groups/{groupId}/students")
    List<Long> getActiveStudentIds(@PathVariable Long groupId);


    @GetMapping("/gms/internal/batches/{batchId}/students/{studentId}/group-id")
    Map<String, Object> myGroupIdRaw(@PathVariable Long batchId, @PathVariable Long studentId);

    default Long getMyGroupId(Long batchId, Long studentId) {
        Object v = myGroupIdRaw(batchId, studentId).get("groupId");
        if (v == null) return null;
        return Long.valueOf(String.valueOf(v));
    }


@GetMapping("/gms/internal/batches/{batchId}/grouping-status")
Map<String, Object> groupingStatusRaw(@PathVariable Long batchId);

default String groupingStatus(Long batchId) {
    Object v = groupingStatusRaw(batchId).get("status");
    return v == null ? "OPEN" : String.valueOf(v);
}

}
