package com.github.sujankumarmitra.notificationservice.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.CreateNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.SynchronousSink;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Slf4j
@Component
public class KafkaCreateNotificationConsumer implements InitializingBean, DisposableBean {

    private final KafkaReceiver<String, String> createNotificationReceiver;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private Disposable kafkaReceiverDisposable;

    public KafkaCreateNotificationConsumer(ReceiverOptions<String, String> receiverOptions,
                                           @Value("${spring.application.name}") String appName,
                                           KafkaProperties kafkaProperties,
                                           ObjectMapper objectMapper,
                                           NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.createNotificationReceiver = KafkaReceiver.create(
                receiverOptions
                        .consumerProperty(ConsumerConfig.GROUP_ID_CONFIG, appName)
                        .commitInterval(Duration.ZERO)
                        .subscription(List.of(kafkaProperties.getCreateNotificationsTopicName())));

        this.notificationService = notificationService;

    }

    @Override
    public void afterPropertiesSet() {
        this.kafkaReceiverDisposable = this.createNotificationReceiver
                .receive()
                .handle(this::createNotification)
                .subscribe();
    }

    @Override
    public void destroy() {
        if (kafkaReceiverDisposable != null)
            kafkaReceiverDisposable.dispose();
    }

    private void createNotification(ReceiverRecord<String, String> _record, SynchronousSink<String> sink) {
        CreateNotificationRequest request = null;
        try {
            request = objectMapper.readValue(_record.value(), CreateNotificationRequest.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize CreateNotificationRequest", e);
        }
        notificationService
                .createNotification(request)
                .subscribe(__ -> acknowledgeKafkaRecord(_record));
    }

    private void acknowledgeKafkaRecord(ReceiverRecord<String, String> _record) {
        _record.receiverOffset().acknowledge();
    }
}
