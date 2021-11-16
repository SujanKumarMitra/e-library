package com.github.sujankumarmitra.assetservice.v1.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@AllArgsConstructor
public final class VerifiedJwtAuthenticationToken implements Authentication {

    private final String subject;
    private final Collection<GrantedAuthority> credentials;
    private final long expiresAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return credentials;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    @Override
    public Object getCredentials() {
        return subject;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return subject;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Operation not allowed");
    }

    @Override
    public String getName() {
        return subject;
    }
}
