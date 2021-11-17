package com.github.sujankumarmitra.authorizationservice.v1.service;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
public interface TokenIntrospector {

    Mono<TokenIntrospectionResponse> introspect(TokenIntrospectionRequest request);

}
