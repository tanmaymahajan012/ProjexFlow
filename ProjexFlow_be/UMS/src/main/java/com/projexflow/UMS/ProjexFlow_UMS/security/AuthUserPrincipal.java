package com.projexflow.UMS.ProjexFlow_UMS.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AuthUserPrincipal implements UserDetails {
    private final AuthUser user;

    public AuthUserPrincipal(AuthUser user) {
        this.user = user;
    }

    public Long getDomainUserId() { return user.getDomainUserId(); }
    public String getEmail() { return user.getEmail(); }
    public String getRole() { return user.getRole().name(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring expects ROLE_ prefix
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isActive(); }
}
