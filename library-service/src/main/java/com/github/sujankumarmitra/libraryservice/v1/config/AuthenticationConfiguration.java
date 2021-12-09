package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@AllArgsConstructor
@Slf4j
@Configuration
@EnableConfigurationProperties(DefaultAuthenticationProperties.class)
public class AuthenticationConfiguration implements InitializingBean {

    private final AuthenticationProperties authenticationProperties;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", authenticationProperties);
    }
}
