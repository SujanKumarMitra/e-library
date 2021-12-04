package com.github.sujankumarmitra.libraryservice.v1.config;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
                title = "Library Service",
                description = "Service for CRUD operations of library data items and managing book leases",
                contact = @Contact(
                        name = "Sujan Kumar Mitra",
                        email = "mitrakumarsujan@gmail.com",
                        url = "https://github.com/SujanKumarMitra/e-library"
                ),
                version = "1.0"
        ),
        security = {
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


    @ApiResponse(
            responseCode = "400",
            description = "Request contains errors",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))
    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiBadRequestResponse {
    }

    @ApiResponse(
            responseCode = "409",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))
    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiConflictResponse {
    }


    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema))
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiNotFoundResponse {
    }


    @ApiResponse(responseCode = "202", description = "Server acknowledged the request")
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiAcceptedResponse {
    }


    @ApiResponse(
            responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "identifier of the created entity",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public @interface ApiCreatedResponse {
    }
}
