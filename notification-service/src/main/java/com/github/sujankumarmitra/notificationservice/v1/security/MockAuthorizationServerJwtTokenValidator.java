package com.github.sujankumarmitra.notificationservice.v1.security;

import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Component
@Profile("dev")
public class MockAuthorizationServerJwtTokenValidator implements JwtTokenValidator {

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(Boolean.TRUE);
    }


    @Data
    static class ResponseBody {
        private boolean active;
    }
}
