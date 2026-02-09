package com.projexflow.pms.ProjexFlow_PMS.identity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.beans.factory.ObjectProvider;


@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private static final String ATTR_STUDENT = "__CURRENT_STUDENT_PROFILE";
    private static final String ATTR_MENTOR_ID = "__CURRENT_MENTOR_ID";
    private static final String ATTR_ADMIN_ID = "__CURRENT_ADMIN_ID";
    private final ObjectProvider<UmsClient> umsClientProvider;
    private UmsClient ums() {
        return umsClientProvider.getObject();
    }
    public String email() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        return principal == null ? null : String.valueOf(principal);
    }

    public String role() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) return null;
        String a = auth.getAuthorities().iterator().next().getAuthority();
        // stored as ROLE_ADMIN / ROLE_STUDENT / ROLE_MENTOR
        return a != null && a.startsWith("ROLE_") ? a.substring("ROLE_".length()) : a;
    }

    public StudentProfile student() {
        HttpServletRequest req = currentRequest();
        if (req != null) {
            Object cached = req.getAttribute(ATTR_STUDENT);
            if (cached instanceof StudentProfile sp) return sp;
        }
        StudentProfile sp = ums().getStudentByEmail(email());
        if (req != null) req.setAttribute(ATTR_STUDENT, sp);
        return sp;
    }

    public Long mentorId() {
        HttpServletRequest req = currentRequest();
        if (req != null) {
            Object cached = req.getAttribute(ATTR_MENTOR_ID);
            if (cached instanceof Long v) return v;
        }
        Long id = ums().getMentorByEmail(email()).getId();
        if (req != null) req.setAttribute(ATTR_MENTOR_ID, id);
        return id;
    }

    public Long adminId() {
        HttpServletRequest req = currentRequest();
        if (req != null) {
            Object cached = req.getAttribute(ATTR_ADMIN_ID);
            if (cached instanceof Long v) return v;
        }
        Long id = ums().getAdminByEmail(email()).getId();
        if (req != null) req.setAttribute(ATTR_ADMIN_ID, id);
        return id;
    }

    public Long roleSpecificId() {
        String r = role();
        if ("STUDENT".equals(r)) return student().getId();
        if ("MENTOR".equals(r)) return mentorId();
        if ("ADMIN".equals(r)) return adminId();
        return null;
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) return null;
        return sra.getRequest();
    }
}
