package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.libraryservice.v1.model.Notification;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class KafkaNotificationService implements NotificationService, DisposableBean {

    private final KafkaSender<String, String> kafkaSender;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendNotification(@NonNull Notification notification) {
        return Mono.create(sink -> {
            String value;

            try {
                value = objectMapper.writeValueAsString(notification);
            } catch (JsonProcessingException ex) {
                log.warn("Error in serializing Notification", ex);
                sink.error(ex);
                return;
            }
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    kafkaProperties.getNotificationsTopicName(),
                    value);

            SenderRecord<String, String, ?> senderRecord = SenderRecord.create(record, null);

            kafkaSender
                    .send(Mono.just(senderRecord))
                    .next()
                    .subscribe(sendResult -> {
                        Exception ex = sendResult.exception();
                        if (ex != null) {
                            log.warn("Error in sending kafka record ", ex);
                            sink.error(ex);
                        } else {
                            log.info("Successfully produced notification in kafka topic");
                            sink.success();
                        }
                    });
        });
    }

    @Override
    public void destroy() {
        kafkaSender.close();
    }
}
