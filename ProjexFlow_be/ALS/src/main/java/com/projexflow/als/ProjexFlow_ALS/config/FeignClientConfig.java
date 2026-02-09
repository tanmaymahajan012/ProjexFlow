package com.projexflow.als.ProjexFlow_ALS.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor forwardAuthHeaders() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                var attrs = RequestContextHolder.getRequestAttributes();
                if (!(attrs instanceof ServletRequestAttributes sra)) return;

                var req = sra.getRequest();
                String auth = req.getHeader("Authorization");
                if (auth != null && !auth.isBlank()) {
                    template.header("Authorization", auth);
                }

                copyIfPresent(req, template, "X-ROLE");
                copyIfPresent(req, template, "X-EMAIL");
            }

            private void copyIfPresent(jakarta.servlet.http.HttpServletRequest req, RequestTemplate template, String name) {
                String v = req.getHeader(name);
                if (v != null && !v.isBlank()) template.header(name, v);
            }
        };
    }
}
