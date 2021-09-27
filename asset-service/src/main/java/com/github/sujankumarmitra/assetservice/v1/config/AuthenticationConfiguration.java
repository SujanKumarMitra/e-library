package com.github.sujankumarmitra.assetservice.v1.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Configuration
@EnableConfigurationProperties(DefaultAuthenticationProperties.class)
public class AuthenticationConfiguration {
}
