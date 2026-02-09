package com.projexflow.mams.ProjexFlow_MAMS.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroupingAccessInterceptor implements HandlerInterceptor {

    private final GroupingAccessGuard guard;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (isExcluded(path)) return true;

        Long batchId = resolveBatchId(request);
        guard.assertAccessible(batchId);

        return true;
    }

    private boolean isExcluded(String path) {
        if (path == null) return false;
        return path.startsWith("/actuator/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources/");
    }

    private Long resolveBatchId(HttpServletRequest request) {
        String role = currentRole();

        // 1) query param
        String q = request.getParameter("batchId");
        Long parsed = parseLong(q);
        if (parsed != null) return parsed;

        // 2) path variable
        Object uriVarsObj = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (uriVarsObj instanceof Map<?, ?> uriVars) {
            Object v = uriVars.get("batchId");
            parsed = parseLong(v == null ? null : String.valueOf(v));
            if (parsed != null) return parsed;
        }

        return null;
    }

    private String currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) return null;
        String a = auth.getAuthorities().iterator().next().getAuthority();
        return a != null && a.startsWith("ROLE_") ? a.substring("ROLE_".length()) : a;
    }

    private Long parseLong(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
