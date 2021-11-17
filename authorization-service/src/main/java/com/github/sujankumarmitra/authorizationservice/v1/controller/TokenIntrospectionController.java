package com.github.sujankumarmitra.authorizationservice.v1.controller;

import com.github.sujankumarmitra.authorizationservice.v1.controller.dto.JacksonTokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.impl.DefaultTokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.service.TokenIntrospector;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@RestController
@AllArgsConstructor
public class TokenIntrospectionController {

    @NonNull
    private final TokenIntrospector tokenIntrospector;

    @PostMapping(path = "/introspect",
            consumes = APPLICATION_FORM_URLENCODED_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public Mono<TokenIntrospectionResponse> introspect(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(this::decodeRequest)
                .flatMap(tokenIntrospector::introspect)
                .map(JacksonTokenIntrospectionResponse::new);
    }

    private Mono<TokenIntrospectionRequest> decodeRequest(MultiValueMap<String, String> formData) {
        return Mono.defer(() -> {
            String token = formData.getFirst("token");
            if (token == null)
                return Mono.error(new ResponseStatusException(BAD_REQUEST, "Missing Token"));

            String tokenTypeHint = formData.getFirst("token_type_hint");

            return Mono.just(new DefaultTokenIntrospectionRequest(token, ofNullable(tokenTypeHint)));
        });

    }

}
