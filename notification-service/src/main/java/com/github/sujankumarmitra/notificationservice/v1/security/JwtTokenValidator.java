package com.github.sujankumarmitra.notificationservice.v1.security;

import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
public interface JwtTokenValidator {

    Mono<Boolean> validateToken(String token);
}
