package com.github.sujankumarmitra.ebookprocessor.v1.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Configuration
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(DefaultRedisProperties.class)
public class RedisConfiguration implements InitializingBean {

    private final RedisProperties redisProperties;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", redisProperties);
    }
}
