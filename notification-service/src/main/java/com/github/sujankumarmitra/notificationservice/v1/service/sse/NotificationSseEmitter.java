package com.github.sujankumarmitra.notificationservice.v1.service.sse;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
public interface NotificationSseEmitter {

    Mono<String> getConsumerId();

    void emitSse(ServerSentEvent<Notification> notificationEvent);

    void emitComplete();

    void emitError(Throwable throwable);
}
