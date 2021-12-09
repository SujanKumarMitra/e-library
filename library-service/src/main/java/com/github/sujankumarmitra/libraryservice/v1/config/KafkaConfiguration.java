package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(DefaultKafkaProperties.class)
public class KafkaConfiguration implements InitializingBean {
    @Autowired
    private KafkaProperties kafkaProperties;

    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", kafkaProperties);
    }

    @Bean
    public KafkaSender<String, String> kafkaSender(KafkaProperties properties) {
        Properties configProps = new Properties();

        configProps.put(BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return KafkaSender.create(SenderOptions.create(configProps));
    }
}
