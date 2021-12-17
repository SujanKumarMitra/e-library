package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaTestConfiguration;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultEBookPermission;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import com.github.sujankumarmitra.libraryservice.v1.util.KafkaTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.*;

import static com.github.sujankumarmitra.libraryservice.v1.util.KafkaTestUtils.createTopics;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.utility.DockerImageName.parse;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = KafkaTestConfiguration.class)
@Testcontainers
@Slf4j
class KafkaEBookPermissionServiceTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(parse("confluentinc/cp-kafka"));
    @MockBean
    private EBookSegmentDao segmentDao;
    @Autowired
    private KafkaSender<String, String> kafkaSender;
    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private AdminClient adminClient;
    private EBookPermissionService permissionService;
    private KafkaConsumer<String, String> kafkaConsumer;

    @BeforeEach
    void setUp() {
        createTopics(kafkaProperties, adminClient);
        createKafkaConsumer();

        permissionService = new KafkaEBookPermissionService(
                kafkaSender,
                kafkaProperties,
                new ObjectMapper(),
                segmentDao
        );
    }

    @AfterEach
    void tearDown() {
        KafkaTestUtils.deleteTopics(kafkaProperties, adminClient);

        kafkaConsumer.close();
        kafkaSender.close();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.bootstrapServers", KAFKA_CONTAINER::getBootstrapServers);
    }


    private void createKafkaConsumer() {

        Properties consumerProps = new Properties();

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        this.kafkaConsumer = new KafkaConsumer<>(consumerProps);

        kafkaConsumer.subscribe(List.of(kafkaProperties.getCreateAssetPermissionsTopicName()),
                new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        log.info("KafkaConsumer has been assigned partitions. Now seeking to beginning");
                        kafkaConsumer.seekToBeginning(partitions);
                    }
                });

    }

    @Test
    void givenValidEBookPermission_whenAssignPermission_shouldAssignPermission() {

        UUID validEBookId = UUID.randomUUID();
        List<R2dbcEBookSegment> segments = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            R2dbcEBookSegment segment = new R2dbcEBookSegment();

            segment.setId(UUID.randomUUID());
            segment.setBookId(validEBookId);
            segment.setIndex(i);
            segment.setAssetId(UUID.randomUUID().toString());

            segments.add(segment);
        }

        Mockito.doReturn(Flux.fromIterable(segments))
                .when(segmentDao).getSegmentsByBookId(validEBookId.toString());

        DefaultEBookPermission permission = new DefaultEBookPermission();

        permission.setBookId(validEBookId.toString());
        permission.setUserId("user_id");
        permission.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        permission.setDurationInMilliseconds(Duration.ofDays(180).toMillis());

        permissionService
                .assignPermission(permission)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));
        int count = records.count();

        assertThat(count).isEqualTo(segments.size());

    }
}