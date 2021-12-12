package com.github.sujankumarmitra.authorizationservice.v1.openapi.schema;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Optional;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Schema(name = "TokenIntrospectionResponse")
public class OpenApiTokenIntrospectionResponse extends TokenIntrospectionResponse {
    @Override
    @Schema(required = true)
    public boolean isActive() {
        return false;
    }

    @Override
    public Optional<String> getSubject() {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<String>> getScopes() {
        return Optional.empty();
    }

    @Override
    public Optional<Long> getExpiry() {
        return Optional.empty();
    }

}
