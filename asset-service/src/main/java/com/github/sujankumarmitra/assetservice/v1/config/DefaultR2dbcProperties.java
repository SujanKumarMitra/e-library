package com.github.sujankumarmitra.assetservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Data
@ConfigurationProperties("spring.r2dbc")
public class DefaultR2dbcProperties extends R2dbcProperties {
    private String url;
    private String username;
    private String password;
}
