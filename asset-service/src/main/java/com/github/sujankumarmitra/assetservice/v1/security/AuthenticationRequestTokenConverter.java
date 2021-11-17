package com.github.sujankumarmitra.assetservice.v1.security;

import com.github.sujankumarmitra.assetservice.v1.exception.MalformedBearerTokenException;
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
public class AuthenticationRequestTokenConverter implements ServerAuthenticationConverter {

    private static final String AUTHORIZATION_PARAM = "Authorization";
    private static final String AUTHORIZATION_PARAM_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_PARAM = "access_token";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getHeaders().containsKey(AUTHORIZATION_PARAM)) {
            String tokenValue = request.getHeaders().getFirst(AUTHORIZATION_PARAM);
            return Mono.create(sink -> {
                if (tokenValue.length() < AUTHORIZATION_PARAM_PREFIX.length())
                    sink.error(new MalformedBearerTokenException(tokenValue));
                sink.success(
                        new AuthenticationRequestToken(
                                tokenValue.substring(AUTHORIZATION_PARAM_PREFIX.length())));
            });
        }

        if (request.getQueryParams().containsKey(ACCESS_TOKEN_PARAM)) {
            String tokenValue = request.getQueryParams().getFirst(ACCESS_TOKEN_PARAM);
            return Mono.just(new AuthenticationRequestToken(tokenValue));
        }

        return Mono.empty();
    }
}
