package com.github.sujankumarmitra.authorizationservice.v1.model;

import java.util.Optional;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
public abstract class TokenIntrospectionRequest {

    public abstract String getToken();

    public abstract Optional<String> getTokenTypeHint();

}
