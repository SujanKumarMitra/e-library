package com.github.sujankumarmitra.assetservice.v1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.COOKIE;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.QUERY;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.APIKEY;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Asset Service",
                description = "Service for storing and hosting static files",
                contact = @Contact(
                        name = "Sujan Kumar Mitra",
                        email = "mitrakumarsujan@gmail.com",
                        url = "https://github.com/SujanKumarMitra/e-library"
                ),
                version = "1.0"
        ),
        security = {
                @SecurityRequirement(name = "cookie"),
                @SecurityRequirement(name = "access_token"),
                @SecurityRequirement(name = "Bearer")
        }
)
@Configuration
public class OpenApiConfiguration {

    @SecurityScheme(
            name = "access_token",
            type = APIKEY,
            in = QUERY
    )
    @SecurityScheme(
            name = "secret",
            type = APIKEY,
            in = COOKIE
    )
    @SecurityScheme(
            name = "Bearer",
            paramName = "Authorization",
            scheme = "bearer",
            type = HTTP,
            bearerFormat = "Bearer "

    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiSecurityScheme {
    }

    @ApiResponse(
            responseCode = "401",
            description = "Token missing or invalid",
            content = @Content(
                    schema = @Schema
            )
    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiUnauthorizedResponse {
    }

    @ApiResponse(
            responseCode = "403",
            description = "Client does not have access to invoke the operation",
            content = @Content(
                    schema = @Schema
            )

    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiForbiddenResponse {
    }


    @ApiUnauthorizedResponse
    @ApiForbiddenResponse
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiSecurityResponse {
    }

}
