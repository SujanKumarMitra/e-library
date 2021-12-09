package com.github.sujankumarmitra.libraryservice.v1.security;

import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author skmitra
 * @since Sep 28/09/21, 2021
 */
@Component
@AllArgsConstructor
public class TokenAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final byte[] responseBody = "{\"errors\":[{\"message\":\"token missing or invalid\"}]}".getBytes();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer responseBodyBuffer = response.bufferFactory().wrap(responseBody);
        Mono<DataBuffer> bufferMono = Mono.just(responseBodyBuffer);

        response.setStatusCode(UNAUTHORIZED);
        response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        return response.writeWith(bufferMono)
                .then();
    }
}
