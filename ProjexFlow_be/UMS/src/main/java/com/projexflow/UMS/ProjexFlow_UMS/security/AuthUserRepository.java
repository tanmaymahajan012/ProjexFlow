package com.projexflow.UMS.ProjexFlow_UMS.security;

import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<AuthUser> findByRoleAndDomainUserId(Role role, Long domainUserId);
}
