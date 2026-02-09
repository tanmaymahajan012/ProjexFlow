package com.projexflow.pms.ProjexFlow_PMS.config;

import com.projexflow.pms.ProjexFlow_PMS.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()

                        // Students create their project after grouping CLOSED; admin can create as well
                        .requestMatchers(HttpMethod.POST, "/pms/projects").hasAnyRole("STUDENT", "ADMIN")

                        // Student's own project view
                        .requestMatchers(HttpMethod.GET, "/pms/projects/my").hasRole("STUDENT")

                        // Lookups by batch+group (used by TMS and mentor dashboards)
                        .requestMatchers(HttpMethod.GET, "/pms/projects").hasAnyRole("STUDENT", "MENTOR", "ADMIN")

                        // Admin updates
                        .requestMatchers(HttpMethod.PATCH, "/pms/projects/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
