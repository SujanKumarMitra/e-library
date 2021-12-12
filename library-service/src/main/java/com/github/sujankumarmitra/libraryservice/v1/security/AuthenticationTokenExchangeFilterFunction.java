package com.github.sujankumarmitra.libraryservice.v1.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Component
public class AuthenticationTokenExchangeFilterFunction implements ExchangeFilterFunction {

    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .cast(AuthenticationToken.class)
                .map(AuthenticationToken::getTokenValue)
                .map(tokenValue -> ClientRequest
                        .from(request)
                        .header(AUTHORIZATION, BEARER_PREFIX + tokenValue)
                        .build())
                .flatMap(next::exchange);
    }
}
