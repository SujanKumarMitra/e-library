package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Configuration
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(EnvironmentServiceRegistry.class)
public class ServiceRegistryConfiguration implements InitializingBean {
    private ServiceRegistry serviceRegistry;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", serviceRegistry);
    }
}
