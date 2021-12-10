package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Data
@Validated
@ConfigurationProperties("spring.r2dbc")
public class DefaultDatabaseProperties extends DatabaseProperties {
    @NotEmpty
    private String url;
    private String username;
    private String password;
}
