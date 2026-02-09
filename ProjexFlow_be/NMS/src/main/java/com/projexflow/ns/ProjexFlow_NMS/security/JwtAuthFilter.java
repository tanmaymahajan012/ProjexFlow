package com.projexflow.ns.ProjexFlow_NMS.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String role = jwtService.getRole(token);
            String email = jwtService.getEmail(token);

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
            var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Ensure X-ROLE/X-EMAIL are available on direct calls (without gateway)
            if (request.getHeader("X-ROLE") == null || request.getHeader("X-EMAIL") == null) {
                HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("X-ROLE".equalsIgnoreCase(name)) return role;
                        if ("X-EMAIL".equalsIgnoreCase(name)) return email;
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        List<String> names = Collections.list(super.getHeaderNames());
                        if (!names.contains("X-ROLE")) names.add("X-ROLE");
                        if (!names.contains("X-EMAIL")) names.add("X-EMAIL");
                        return Collections.enumeration(names);
                    }
                };
                filterChain.doFilter(wrapped, response);
                return;
            }

        } catch (Exception ignored) {
            // ignore invalid token and let the security chain handle it
        }

        filterChain.doFilter(request, response);
    }
}
