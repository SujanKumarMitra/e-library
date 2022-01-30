package com.github.sujankumarmitra.notificationservice.v1.security;

import com.github.sujankumarmitra.notificationservice.v1.config.InternalUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptySet;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Component
@AllArgsConstructor
public class InternalAuthenticationManager implements ReactiveAuthenticationManager {

    @NonNull
    private final InternalUser internalUser;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            String username = authentication.getName();
            String password = (String) authentication.getCredentials();

            if(matches(username,password)) {
                return Mono.just(new UsernamePasswordAuthenticationToken(username,password, emptySet()));
            } else {
                return Mono.empty();
            }
        });
    }

    private boolean matches(String username, String password) {
        return username.equals(internalUser.getUsername()) &&
                password.equals(internalUser.getPassword());
    }
}
