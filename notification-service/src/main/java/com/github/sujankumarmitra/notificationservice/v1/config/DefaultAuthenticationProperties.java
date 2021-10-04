package com.github.sujankumarmitra.notificationservice.v1.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Data
@ConfigurationProperties
public class DefaultAuthenticationProperties extends AuthenticationProperties {
    @Value("${app.auth.baseUrl}")
    private String baseUrl;
}