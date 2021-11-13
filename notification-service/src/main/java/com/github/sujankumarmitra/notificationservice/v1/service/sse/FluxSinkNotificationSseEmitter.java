package com.github.sujankumarmitra.notificationservice.v1.service.sse;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
public class FluxSinkNotificationSseEmitter extends AbstractNotificationSseEmitter {

    private final FluxSink<ServerSentEvent<Notification>> sseSink;

    public FluxSinkNotificationSseEmitter(Mono<String> consumerId, FluxSink<ServerSentEvent<Notification>> sink) {
        super(consumerId);
        sseSink = sink;
    }

    @Override
    public void emitSse(ServerSentEvent<Notification> notificationEvent) {
        sseSink.next(notificationEvent);
    }

    @Override
    public void emitComplete() {
        sseSink.complete();
    }

    @Override
    public void emitError(Throwable throwable) {
        sseSink.error(throwable);
    }
}
