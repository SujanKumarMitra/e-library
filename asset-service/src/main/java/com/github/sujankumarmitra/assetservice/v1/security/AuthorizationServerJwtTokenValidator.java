package com.github.sujankumarmitra.assetservice.v1.security;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.lang.Boolean.TRUE;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
public class AuthorizationServerJwtTokenValidator implements JwtTokenValidator {
    @Override
    public Mono<Boolean> validateToken(String token) {
        // TODO hook with real auth server
        return Mono.just(TRUE);
    }
}
