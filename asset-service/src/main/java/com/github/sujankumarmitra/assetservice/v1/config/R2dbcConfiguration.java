package com.github.sujankumarmitra.assetservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Configuration
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(DefaultR2dbcProperties.class)
public class R2dbcConfiguration implements InitializingBean {

    private R2dbcProperties r2dbcProperties;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", r2dbcProperties);
    }
}
