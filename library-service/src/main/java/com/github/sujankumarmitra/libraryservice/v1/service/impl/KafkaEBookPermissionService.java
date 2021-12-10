package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookPermission;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class KafkaEBookPermissionService implements EBookPermissionService {

    private final KafkaSender<String, String> kafkaSender;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;
    private final EBookSegmentDao ebookSegmentDao;

    @Override
    public Mono<Void> assignPermission(EBookPermission ebookPermission) {

        return ebookSegmentDao
                .getSegmentsByBookId(ebookPermission.getBookId())
                .map(segment -> mapToAssetPermission(ebookPermission, segment))
                .handle(this::serializeToJson)
                .map(this::mapToSenderRecord)
                .as(kafkaSender::send)
                .handle(this::handleSendResult)
                .then();

    }

    @NonNull
    private AssetPermission mapToAssetPermission(EBookPermission ebookPermission, EBookSegment segment) {
        return new AssetPermission(segment.getAssetId(),
                ebookPermission.getUserId(),
                ebookPermission.getStartTimeInEpochMilliseconds(),
                ebookPermission.getDurationInMilliseconds());
    }

    private void handleSendResult(SenderResult<Void> result, SynchronousSink<Void> sink) {
        Exception ex = result.exception();
        if (ex != null) {
            log.warn("SendResult has exception", ex);
        } else {
            log.info("Successfully produced record to kafka topic {}", result.recordMetadata().topic());
        }
    }

    private SenderRecord<String, String, Void> mapToSenderRecord(String jsonBody) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(kafkaProperties.getCreateAssetPermissionsTopicName(), jsonBody);
        return SenderRecord.create(producerRecord, null);
    }

    private void serializeToJson(AssetPermission permission, SynchronousSink<String> sink) {
        try {
            sink.next(objectMapper.writeValueAsString(permission));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize EBookPermission", e);
            sink.error(new InternalError(e.getMessage()));
        }
    }

    @Data
    @AllArgsConstructor
    private static class AssetPermission {
        private final String assetId;
        private final String subjectId;
        private final Long grantStartEpochMilliseconds;
        private final Long grantDurationInMilliseconds;
    }
}
