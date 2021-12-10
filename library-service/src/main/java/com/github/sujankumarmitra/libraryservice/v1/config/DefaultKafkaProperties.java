package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Data
@Validated
@ConfigurationProperties("app.kafka")
public class DefaultKafkaProperties extends KafkaProperties {
    @NotEmpty
    private String bootstrapServers;
    @NotEmpty
    private String notificationsTopicName;
}
