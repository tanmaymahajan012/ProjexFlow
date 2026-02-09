package com.projexflow.als.ProjexFlow_ALS.config;

import com.projexflow.als.ProjexFlow_ALS.security.JwtAuthFilter;
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

                        // Create a log entry (used by UI and other services)
                        .requestMatchers(HttpMethod.POST, "/als/logs").hasAnyRole("STUDENT", "MENTOR", "ADMIN")

                        // User can view their own logs
                        .requestMatchers(HttpMethod.GET, "/als/logs/my").hasAnyRole("STUDENT", "MENTOR", "ADMIN")

                        // Admin/Mentor can search + view all logs
                        .requestMatchers(HttpMethod.GET, "/als/logs").hasAnyRole("MENTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/als/logs/**").hasAnyRole("MENTOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
