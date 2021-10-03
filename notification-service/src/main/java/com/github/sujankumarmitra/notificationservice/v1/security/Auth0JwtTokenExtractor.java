package com.github.sujankumarmitra.notificationservice.v1.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
public class Auth0JwtTokenExtractor implements JwtTokenExtractor {

    @Override
    public Mono<Authentication> extractToken(String token) {
        return Mono.just(token)
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::extract);
    }

    private Authentication extract(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);

        String username = decodedJWT.getSubject();
        List<GrantedAuthority> scopes = decodedJWT
                .getClaim("scopes")
                .asList(String.class)
                .parallelStream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, "", scopes);
    }
}
