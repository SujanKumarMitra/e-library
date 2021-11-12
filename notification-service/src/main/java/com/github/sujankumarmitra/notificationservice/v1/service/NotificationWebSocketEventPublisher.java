package com.github.sujankumarmitra.notificationservice.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import com.github.sujankumarmitra.notificationservice.v1.service.socket.WebSocketSessionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * @author skmitra
 * @since Oct 22/10/21, 2021
 */
@Component
@AllArgsConstructor
@Slf4j
public class NotificationWebSocketEventPublisher implements InitializingBean {

    @NonNull
    private final NotificationEventService eventService;
    @NonNull
    private final WebSocketSessionService sessionService;
    @NonNull
    private final ObjectMapper mapper;

    private void sendMessage(Tuple2<String, WebSocketSession> tuple) {
        String jsonBody = tuple.getT1();
        WebSocketSession session = tuple.getT2();

        WebSocketMessage message = session.textMessage(jsonBody);
        session.send(Mono.just(message))
                .subscribe();
    }

    @Override
    public void afterPropertiesSet() {
        eventService.consumeEvents()
                .concatMap(this::concatSessions)
                .handle(this::serializeToJson)
                .subscribe(this::sendMessage);
    }

    private Flux<Tuple2<Notification, WebSocketSession>> concatSessions(Notification notification) {
        return sessionService.getSessions(notification.getConsumerId())
                .map(session -> Tuples.of(notification, session));
    }

    private void serializeToJson(Tuple2<Notification, WebSocketSession> tuple, SynchronousSink<Tuple2<String, WebSocketSession>> sink) {
        try {
            String jsonBody = mapper.writeValueAsString(tuple.getT1());
            Tuple2<String, WebSocketSession> jsonSessionTuple = Tuples.of(jsonBody, tuple.getT2());
            sink.next(jsonSessionTuple);
        } catch (JsonProcessingException e) {
            log.warn("Error in deserialization Notification", e);
        }
    }
}
