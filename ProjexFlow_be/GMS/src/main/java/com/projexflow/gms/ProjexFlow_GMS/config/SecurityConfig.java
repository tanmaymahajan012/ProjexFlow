package com.projexflow.gms.ProjexFlow_GMS.config;

import com.projexflow.gms.ProjexFlow_GMS.security.JwtAuthFilter;
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

                        // Student creates request
                        .requestMatchers(HttpMethod.POST, "/gms/requests").hasAnyRole("STUDENT", "ADMIN")

                        // Recipient student accepts/rejects
                        .requestMatchers(HttpMethod.POST, "/gms/requests/*/accept").hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/gms/requests/*/reject").hasAnyRole("STUDENT", "ADMIN")

                        // UI: group details with full member profiles
                        .requestMatchers(HttpMethod.GET, "/gms/groups/*").hasAnyRole("STUDENT","MENTOR","ADMIN")

                        // Internal read endpoints used by other services
                        .requestMatchers(HttpMethod.GET, "/gms/internal/**").hasAnyRole("STUDENT","MENTOR","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/gms/internal/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

