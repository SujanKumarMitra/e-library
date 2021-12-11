package com.github.sujankumarmitra.ebookprocessor.v1.security;

import com.github.sujankumarmitra.ebookprocessor.v1.config.AuthenticationProperties;
import lombok.Data;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

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
                .map(response -> buildToken(token, response));
    }

    private AuthenticationToken buildToken(String token, IntrospectionResponse response) {
        String subject = response.sub;
        Collection<GrantedAuthority> scopes = response.scopes
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());

        long expiresAt = response.exp;

        return new AuthenticationToken(token, subject, scopes, expiresAt);
    }


    @Data
    static class IntrospectionResponse {
        private boolean active;
        private final String sub;
        private final Collection<String> scopes;
        private final Long exp;
    }
}
