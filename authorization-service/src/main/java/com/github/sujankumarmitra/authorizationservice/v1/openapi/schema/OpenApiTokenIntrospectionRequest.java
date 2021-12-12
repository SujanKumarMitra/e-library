package com.github.sujankumarmitra.authorizationservice.v1.openapi.schema;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Schema(name = "TokenIntrospectionRequest")
public class OpenApiTokenIntrospectionRequest extends TokenIntrospectionRequest {
    @Override
    @Schema(required = true)
    public String getToken() {
        return null;
    }

    @Override
    public Optional<String> getTokenTypeHint() {
        return Optional.empty();
    }
}
