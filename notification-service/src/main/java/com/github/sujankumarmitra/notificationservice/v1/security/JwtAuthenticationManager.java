package com.github.sujankumarmitra.notificationservice.v1.security;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @NonNull
    private JwtTokenValidator tokenValidator;
    @NonNull
    private JwtTokenExtractor tokenExtractor;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        return tokenValidator.validateToken(token)
                .then(tokenExtractor.extractToken(token));
    }
}
