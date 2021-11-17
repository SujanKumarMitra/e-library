package com.github.sujankumarmitra.authorizationservice.v1.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer ignoreEmptyOptionalCustomizer() {
        return builder -> builder.modulesToInstall(new Jdk8Module());
    }
}
