package com.github.sujankumarmitra.libraryservice.v1.security;

import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
public interface TokenIntrospector {

    Mono<AuthenticationToken> introspectToken(String token);
}
