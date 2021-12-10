package com.github.sujankumarmitra.libraryservice.v1.config;

import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.apache.kafka.clients.admin.AdminClient.create;
import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Configuration
public class KafkaTestConfiguration {

    @Bean
    public AdminClient kafkaAdminClient(KafkaProperties kafkaProperties) {
        return create(singletonMap(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()));
    }
}
