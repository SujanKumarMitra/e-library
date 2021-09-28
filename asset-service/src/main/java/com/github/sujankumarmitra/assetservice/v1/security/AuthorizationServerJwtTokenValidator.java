package com.github.sujankumarmitra.assetservice.v1.security;

import com.github.sujankumarmitra.assetservice.v1.config.AuthenticationProperties;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
@Profile("!dev")
public class AuthorizationServerJwtTokenValidator implements JwtTokenValidator {

    private final WebClient webClient;

    public AuthorizationServerJwtTokenValidator(WebClient.Builder webClientBuilder, AuthenticationProperties properties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return webClient
                .post()
                .uri("/introspect")
                .body(fromFormData("token", token))
                .retrieve()
                .bodyToMono(ResponseBody.class)
                .map(ResponseBody::isActive);
    }


    @Data
    static class ResponseBody {
        private boolean active;
    }
}
