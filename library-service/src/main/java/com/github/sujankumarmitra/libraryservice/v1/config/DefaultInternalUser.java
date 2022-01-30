package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Data
@ConfigurationProperties("app.auth.internal-user")
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class DefaultInternalUser extends InternalUser {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
