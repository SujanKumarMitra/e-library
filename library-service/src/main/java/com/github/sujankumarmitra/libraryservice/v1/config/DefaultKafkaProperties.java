package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Data
@ConfigurationProperties("app.kafka")
public class DefaultKafkaProperties extends KafkaProperties {
    private String bootstrapServers;
    private String notificationsTopicName;
}
