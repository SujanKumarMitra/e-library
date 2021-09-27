package com.github.sujankumarmitra.assetservice.v1.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;

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
 * @since Sep 27/09/21, 2021
 */
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
