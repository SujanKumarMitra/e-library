package com.github.sujankumarmitra.ebookprocessor.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Data
@Validated
@ConfigurationProperties("app.redis")
public class DefaultRedisProperties extends RedisProperties {
    @NotEmpty
    private String url;
    private String password;
    @Positive
    private Long defaultKeyExpirationInMilliseconds;
}
