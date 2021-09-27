package com.github.sujankumarmitra.assetservice.v1.security;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
public interface JwtTokenExtractor {

    Mono<Authentication> extractToken(String token);
}
