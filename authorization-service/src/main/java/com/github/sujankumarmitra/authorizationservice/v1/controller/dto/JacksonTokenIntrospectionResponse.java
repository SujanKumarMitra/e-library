package com.github.sujankumarmitra.authorizationservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@AllArgsConstructor
@JsonInclude(NON_ABSENT)
public class JacksonTokenIntrospectionResponse extends TokenIntrospectionResponse {

    @NonNull
    private final TokenIntrospectionResponse response;

    @Override
    @JsonProperty("valid")
    public boolean isValid() {
        return response.isValid();
    }

    @Override
    @JsonProperty("sub")
    public Optional<String> getSubject() {
        return response.getSubject();
    }

    @Override
    @JsonProperty("scopes")
    public Optional<Collection<String>> getScopes() {
        return response.getScopes();
    }

    @Override
    @JsonProperty("exp")
    public Optional<Long> getExpiry() {
        return response.getExpiry();
    }

    @Override
    @JsonProperty("nbf")
    public Optional<Long> getNotBefore() {
        return response.getNotBefore();
    }
}
