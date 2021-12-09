package com.github.sujankumarmitra.assetservice.v1.security;

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
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

    @NonNull
    private final TokenIntrospector tokenIntrospector;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (!(authentication instanceof AuthenticationRequestToken))
            return Mono.empty();

        String token = ((AuthenticationRequestToken) authentication).getTokenValue();
        return tokenIntrospector
                .introspectToken(token)
                .cast(Authentication.class);
    }
}
