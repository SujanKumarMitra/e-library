package com.github.sujankumarmitra.assetservice.v1.security;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
public class JwtTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

//    TODO externalize token extraction keys to make it configurable

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getCookies().containsKey("Authorization")) {
            HttpCookie tokenCookie = request.getCookies().getFirst("Authorization");
            return Mono.just(new JwtAuthenticationToken(tokenCookie.getValue()));
        }

        if (request.getHeaders().containsKey("Authorization")) {
            String tokenValue = request.getHeaders().getFirst("Authorization");
            return Mono.just(new JwtAuthenticationToken(tokenValue));
        }

        if (request.getQueryParams().containsKey("token")) {
            String tokenValue = request.getQueryParams().getFirst("token");
            return Mono.just(new JwtAuthenticationToken(tokenValue));
        }

        return Mono.empty();
    }
}
