package com.github.sujankumarmitra.notificationservice.v1.service.sse;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.*;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@Service
public class MapNotificationSseEmitterService implements NotificationSseEmitterService {

    private final Map<String, Set<NotificationSseEmitter>> emitterMap;

    public MapNotificationSseEmitterService() {
        this(Collections.synchronizedMap(new HashMap<>()));
    }

    public MapNotificationSseEmitterService(Map<String, Set<NotificationSseEmitter>> emitterMap) {
        this.emitterMap = emitterMap;
    }


    @Override
    public Mono<Void> addEmitter(NotificationSseEmitter emitter) {
        return emitter.getConsumerId()
                .handle((consumerId, sink) -> addEmitterToMap(consumerId, emitter, sink));
    }

    private void addEmitterToMap(String consumerId, NotificationSseEmitter emitter, SynchronousSink<Void> sink) {
        emitterMap.compute(consumerId, (__, emitters) -> {
            if (emitters == null)
                emitters = Collections.synchronizedSet(new HashSet<>());
            emitters.add(emitter);
            return emitters;
        });
        sink.complete();
    }

    @Override
    public Mono<Void> removeEmitter(NotificationSseEmitter emitter) {
        return emitter.getConsumerId()
                .handle((consumerId, sink) -> removeEmitterFromMap(consumerId, emitter, sink));
    }

    private void removeEmitterFromMap(String consumerId, NotificationSseEmitter emitter, SynchronousSink<Void> sink) {
        emitterMap.compute(consumerId, (__, emitters) -> {
            if (emitters == null) return null;
            emitters.remove(emitter);
            if (emitters.size() == 0) return null;
            return emitters;
        });
        sink.complete();
    }

    @Override
    public Flux<NotificationSseEmitter> getEmitters(String consumerId) {
        Set<NotificationSseEmitter> sseEmitters = emitterMap.getOrDefault(consumerId, Collections.emptySet());
        return Flux.fromIterable(sseEmitters);
    }
}
