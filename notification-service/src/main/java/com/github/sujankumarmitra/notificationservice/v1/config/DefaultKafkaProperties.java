package com.github.sujankumarmitra.notificationservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Data
@ConfigurationProperties("app.kafka")
public class DefaultKafkaProperties extends KafkaProperties {

    private String bootstrapServers;
    private String notificationTopicName;
}
