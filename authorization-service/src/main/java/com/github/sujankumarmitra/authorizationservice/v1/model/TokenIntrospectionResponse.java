package com.github.sujankumarmitra.authorizationservice.v1.model;

import java.util.Collection;
import java.util.Optional;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
public abstract class TokenIntrospectionResponse {

    public abstract boolean isActive();

    public abstract Optional<String> getSubject();

    public abstract Optional<Collection<String>> getScopes();

    public abstract Optional<Long> getExpiry();

}