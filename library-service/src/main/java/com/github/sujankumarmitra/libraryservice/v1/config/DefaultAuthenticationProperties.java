package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Data
@Validated
@ConfigurationProperties("app.auth")
public class DefaultAuthenticationProperties extends AuthenticationProperties {
    @NotEmpty
    private String baseUrl;
}