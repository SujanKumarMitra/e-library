package com.github.sujankumarmitra.notificationservice.v1.security;

import com.github.sujankumarmitra.notificationservice.v1.config.AuthenticationProperties;
import lombok.Data;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
public class AuthorizationServerTokenIntrospector implements TokenIntrospector {

    private final WebClient webClient;

    public AuthorizationServerTokenIntrospector(WebClient.Builder webClientBuilder, AuthenticationProperties properties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<AuthenticationToken> introspectToken(String token) {
        return webClient
                .post()
                .uri("/introspect")
                .body(fromFormData("token", token))
                .retrieve()
                .bodyToMono(IntrospectionResponse.class)
                .filter(IntrospectionResponse::isActive)
                .switchIfEmpty(Mono.error(new CredentialsExpiredException("Token is invalid")))
                .map(this::buildToken);
    }

    private AuthenticationToken buildToken(IntrospectionResponse response) {
        String subject = response.sub.get();
        Collection<GrantedAuthority> scopes = response.scopes
                .get()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());

        long expiresAt = response.exp.get();

        return new AuthenticationToken(subject, scopes, expiresAt);
    }


    @Data
    static class IntrospectionResponse {
        private boolean active;
        private final Optional<String> sub;
        private final Optional<Collection<String>> scopes;
        private final Optional<Long> exp;
    }
}
