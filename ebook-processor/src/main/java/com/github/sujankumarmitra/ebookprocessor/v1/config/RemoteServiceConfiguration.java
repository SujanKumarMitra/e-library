package com.github.sujankumarmitra.ebookprocessor.v1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(EnvironmentRemoteServiceRegistry.class)
public class RemoteServiceConfiguration implements InitializingBean {
    @Autowired
    private RemoteServiceRegistry registry;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", registry);
    }
}
