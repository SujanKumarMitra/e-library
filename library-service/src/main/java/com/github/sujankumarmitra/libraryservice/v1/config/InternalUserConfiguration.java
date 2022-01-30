package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Configuration
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(DefaultInternalUser.class)
public class InternalUserConfiguration {
    @NonNull
    private final InternalUser internalUser;

    @PostConstruct
    public void onInit() {
        log.info("Using {}", internalUser);
    }
}
