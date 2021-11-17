package com.github.sujankumarmitra.authorizationservice.v1.controller;

import com.github.sujankumarmitra.authorizationservice.v1.controller.dto.JacksonTokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.openapi.schema.OpenAPITokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.openapi.schema.OpenAPITokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.impl.DefaultTokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.service.TokenIntrospector;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "TokenIntrospectionController")
@RestController
@AllArgsConstructor
public class TokenIntrospectionController {

    @NonNull
    private final TokenIntrospector tokenIntrospector;

    @Operation(
            description = "# Validate a JWT token",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = APPLICATION_FORM_URLENCODED_VALUE,
                            schema = @Schema(implementation = OpenAPITokenIntrospectionRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful request",
                            content = @Content(schema = @Schema())
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Missing token",
                            content = @Content(
                                    schema = @Schema(implementation = OpenAPITokenIntrospectionResponse.class))
                    )
            }
    )
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
