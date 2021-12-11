package com.github.sujankumarmitra.ebookprocessor.v1.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@AllArgsConstructor
@Getter
public final class AuthenticationRequestToken implements Authentication {

    @NonNull
    private final String tokenValue;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return emptyList();
    }

    @Override
    public Object getCredentials() {
        return tokenValue;
    }

    @Override
    public Object getDetails() {
        return tokenValue;
    }

    @Override
    public Object getPrincipal() {
        return tokenValue;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Operation not allowed here.");
    }

    @Override
    public String getName() {
        return tokenValue;
    }
}
