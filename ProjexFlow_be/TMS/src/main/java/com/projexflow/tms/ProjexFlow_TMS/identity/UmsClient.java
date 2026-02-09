package com.projexflow.tms.ProjexFlow_TMS.identity;

import com.projexflow.tms.ProjexFlow_TMS.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "ums",
        url = "${ums.service.url}",
        configuration = FeignClientConfig.class
)
public interface UmsClient {

    @GetMapping("/api/v1/internal/students/by-email")
    StudentProfile getStudentByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/internal/mentors/by-email")
    IdOnlyResponse getMentorByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/internal/admins/by-email")
    IdOnlyResponse getAdminByEmail(@RequestParam("email") String email);
}
