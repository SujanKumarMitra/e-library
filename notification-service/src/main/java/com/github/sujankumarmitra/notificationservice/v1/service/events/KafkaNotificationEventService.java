package com.github.sujankumarmitra.notificationservice.v1.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.notificationservice.v1.model.JacksonNewNotificationEvent;
import com.github.sujankumarmitra.notificationservice.v1.model.NewNotificationEvent;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;

import static reactor.kafka.sender.SenderRecord.create;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Service
@AllArgsConstructor
public class KafkaNotificationEventService implements NotificationEventService {

    @NonNull
    private final KafkaSender<String, String> kafkaSender;
    @NonNull
    private final KafkaReceiver<String, String> kafkaReceiver;
    @NonNull
    private final ObjectMapper mapper;
    @NonNull
    private KafkaProperties properties;

    @Override
    public Mono<Void> publishEvent(NewNotificationEvent event) {
        return Mono.just(event)
                .handle(this::serialize)
                .map(value -> new ProducerRecord<String, String>(properties.getNotificationTopicName(), value))
                .map(record -> create(record, event.getNotificationId()))
                .as(kafkaSender::send)
                .next()
                .handle((sendResult, sink) -> {
                    if (sendResult.exception() != null)
                        sink.error(sendResult.exception());
                    sink.complete();
                });

    }

    @Override
    public Flux<NewNotificationEvent> consumeEvents() {
        return kafkaReceiver
                .receive()
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .map(ConsumerRecord::value)
                .handle(this::deserialize);
    }

    private void serialize(NewNotificationEvent event, SynchronousSink<String> sink) {
        try {
            sink.next(mapper.writeValueAsString(event));
            sink.complete();
        } catch (Throwable ex) {
            sink.error(ex);
        }
    }

    private void deserialize(String value, SynchronousSink<NewNotificationEvent> sink) {
        try {
            sink.next(mapper.readValue(value, JacksonNewNotificationEvent.class));
        } catch (Throwable th) {
            sink.error(th);
        }
    }
}
