package com.github.sujankumarmitra.notificationservice.v1.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.notificationservice.v1.model.DefaultNotification;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;

import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.function.Function;

import static java.time.Duration.ZERO;
import static reactor.kafka.sender.SenderRecord.create;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Service
@Slf4j
public class KafkaNotificationEventService implements NotificationEventService, DisposableBean {

    private final KafkaSender<String, String> kafkaSender;
    private final Disposable notificationEventFluxDisposable;
    private Flux<Notification> notificationEventFlux;
    private final ObjectMapper mapper;
    private final KafkaProperties properties;

    public KafkaNotificationEventService(@NonNull KafkaSender<String, String> kafkaSender,
                                         @NonNull ReceiverOptions<String, String> receiverOptions,
                                         @NonNull ObjectMapper mapper,
                                         @NonNull KafkaProperties properties) {
        this.kafkaSender = kafkaSender;
        this.mapper = mapper;
        this.properties = properties;
        ConnectableFlux<Notification> notificationEventConnectableFlux = KafkaReceiver.create(receiverOptions
                        .commitBatchSize(0)
                        .commitInterval(ZERO)
                        .subscription(Collections.singleton(properties.getNotificationTopicName())))
                .receiveAutoAck()
                .concatMap(Function.identity())
                .map(ConsumerRecord::value)
                .handle(this::deserialize)
                .publish();

        this.notificationEventFlux = notificationEventConnectableFlux;
        this.notificationEventFluxDisposable = notificationEventConnectableFlux.connect();

    }

    @Override
    public Mono<Void> publishEvent(Notification event) {
        return Mono.just(event)
                .handle(this::serialize)
                .map(value -> new ProducerRecord<String, String>(properties.getNotificationTopicName(), value))
                .map(_record -> create(_record, event.getId()))
                .as(kafkaSender::send)
                .next()
                .handle((sendResult, sink) -> {
                    if (sendResult.exception() != null)
                        sink.error(sendResult.exception());
                    sink.complete();
                });

    }

    @Override
    public Flux<Notification> consumeEvents() {
        return this.notificationEventFlux;
    }

    private void serialize(Notification event, SynchronousSink<String> sink) {
        try {
            sink.next(mapper.writeValueAsString(event));
            sink.complete();
        } catch (Exception ex) {
            sink.error(ex);
        }
    }

    private void deserialize(String value, SynchronousSink<Notification> sink) {
        try {
            sink.next(mapper.readValue(value, DefaultNotification.class));
        } catch (Exception th) {
            sink.error(th);
        }
    }

    @Override
    public void destroy() {
        kafkaSender.close();
        try {
            notificationEventFluxDisposable.dispose();
        } catch (CancellationException e) {
            // ignore
        }
    }
}
