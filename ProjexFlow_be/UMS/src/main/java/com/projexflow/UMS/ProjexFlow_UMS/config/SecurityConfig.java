package com.projexflow.UMS.ProjexFlow_UMS.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/*").permitAll()

                        // RBAC example (adjust to your real needs)
                        .requestMatchers("/api/v1/admins/**").hasRole("ADMIN")
                        // Internal cross-service queries (GMS/TMS) need UI-friendly student/mentor info.
                        .requestMatchers("/api/v1/internal/**").hasAnyRole("STUDENT","MENTOR","ADMIN")
                        .requestMatchers("/api/v1/mentors/**").hasRole("ADMIN")   // or ADMIN+MENTOR depending on your rules
                        // Allow students to manage their own profile (UI) while keeping admin access
                        .requestMatchers(HttpMethod.PUT, "/api/v1/students/*/profile-photo").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/students/*").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/*").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers("/api/v1/students/**").hasRole("ADMIN")  // admin-only bulk/list/create
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/batch-ids").hasAnyRole("ADMIN", "MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/courses")
                        .hasAnyRole("ADMIN","MENTOR")

                        // everything else needs login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
