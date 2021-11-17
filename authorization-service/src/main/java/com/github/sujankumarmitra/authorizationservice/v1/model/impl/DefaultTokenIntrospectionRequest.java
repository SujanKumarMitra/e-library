package com.github.sujankumarmitra.authorizationservice.v1.model.impl;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import lombok.NonNull;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
public class DefaultTokenIntrospectionRequest extends TokenIntrospectionRequest {

    private final String token;
    private final Optional<String> tokenTypeHint;

    public DefaultTokenIntrospectionRequest(@NonNull String token) {
        this(token, empty());
    }

    public DefaultTokenIntrospectionRequest(@NonNull String token, @NonNull Optional<String> tokenTypeHint) {
        this.token = token;
        this.tokenTypeHint = tokenTypeHint;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public Optional<String> getTokenTypeHint() {
        return this.tokenTypeHint;
    }
}
