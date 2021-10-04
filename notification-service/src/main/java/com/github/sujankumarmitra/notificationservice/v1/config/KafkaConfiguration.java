package com.github.sujankumarmitra.notificationservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Configuration
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(DefaultKafkaProperties.class)
public class KafkaConfiguration {

    @NonNull
    private KafkaProperties kafkaProperties;

    @Bean
    public KafkaSender<String, String> kafkaSender() {
        return KafkaSender.create(senderOptions());
    }

    @Bean
    public SenderOptions<String, String> senderOptions() {
        Map<String, Object> producerProps = new HashMap<>();

        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return SenderOptions.create(producerProps);
    }

    @Bean
    public ReceiverOptions<String, String> receiverOptions() {
        Map<String, Object> consumerProps = new HashMap<>();

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        return ReceiverOptions.create(consumerProps);
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver() {
        ReceiverOptions<String, String> receiverOptions = receiverOptions()
                .subscription(List.of(kafkaProperties.getNotificationTopicName()))
                .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToEnd));

        return KafkaReceiver.create(receiverOptions);
    }


    @PostConstruct
    private void init() {
        log.info("Using {}", kafkaProperties);
    }
}
