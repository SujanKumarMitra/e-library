package com.github.sujankumarmitra.libraryservice.v1.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * @author skmitra
 * @since Jan 27/01/22, 2022
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static boolean hasAuthority(Authentication authentication, String authority) {
        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(presentAuthority -> presentAuthority.equals(authority));
    }

    public static Mono<Boolean> hasAuthority(String authority) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> hasAuthority(authentication, authority));
    }

    public static boolean hasAnyAuthority(Authentication authentication, String... authorities) {
        return hasAnyAuthority(authentication, asList(authorities));
    }

    public static boolean hasAnyAuthority(Authentication authentication, Collection<String> authorities) {
        return authorities.stream()
                .anyMatch(authority -> hasAuthority(authentication, authority));
    }

    public static Mono<Boolean> hasAnyAuthority(String... authorities) {
        return hasAnyAuthority(asList(authorities));
    }

    public static Mono<Boolean> hasAnyAuthority(Collection<String> authorities) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> hasAnyAuthority(authentication, authorities));
    }

}
