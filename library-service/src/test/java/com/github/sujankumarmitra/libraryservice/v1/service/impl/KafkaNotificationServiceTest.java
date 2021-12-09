package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaConfiguration;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.kafka.sender.KafkaSender;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.utility.DockerImageName.parse;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Slf4j
@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = KafkaConfiguration.class)
class KafkaNotificationServiceTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER;
    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private KafkaSender kafkaSender;
    private KafkaConsumer<String, String> kafkaConsumer;
    private KafkaNotificationService notificationService;
    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        DockerImageName confluentKafka = parse("confluentinc/cp-server:6.2.0")
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");
        KAFKA_CONTAINER = new KafkaContainer(confluentKafka);
    }

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.bootstrapServers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("app.kafka.notificationsTopicName", () -> "notifications");
    }

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        createNotificationTopic();
        createKafkaConsumer();
        this.notificationService = new KafkaNotificationService(kafkaSender, kafkaProperties, objectMapper);
    }

    @AfterEach
    void tearDown() {
        kafkaSender.close();
        kafkaConsumer.close();
    }

    private void createKafkaConsumer() {

        Properties consumerProps = new Properties();

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        this.kafkaConsumer = new KafkaConsumer<>(consumerProps);

        kafkaConsumer.subscribe(List.of(kafkaProperties.getNotificationsTopicName()),
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

    private void createNotificationTopic() throws InterruptedException, ExecutionException {
        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        AdminClient adminClient = AdminClient.create(adminProps);

        NewTopic notificationTopic = new NewTopic(
                kafkaProperties.getNotificationsTopicName(),empty(), empty());

        adminClient.createTopics(Set.of(notificationTopic))
                .all()
                .whenComplete((v, th) -> log.info("AdminClient created topics"))
                .get();
    }

    @Test
    void testAllComponentsAutowired() {
        assertThat(kafkaSender).isNotNull();
        assertThat(kafkaProperties).isNotNull();
    }


    @Test
    void givenValidNotification_whenSend_shouldSend() throws JsonProcessingException {
        DefaultNotification expectedNotification = new DefaultNotification();

        expectedNotification.setConsumerId("consumer_id");
        expectedNotification.setCreatedAt(System.currentTimeMillis());
        expectedNotification.setPayload("{ \"foo\": \"bar\" }");

        notificationService
                .sendNotification(expectedNotification)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();


        String recordValue = null;
        for(ConsumerRecord<String, String> consumerRecord: kafkaConsumer.poll(Duration.ofSeconds(10))) {
            recordValue = consumerRecord.value();
            kafkaConsumer.commitSync();
            break;
        }

        assertThat(recordValue).isNotNull();

        DefaultNotification actualNotification = objectMapper.readValue(recordValue, DefaultNotification.class);
        assertThat(actualNotification).isEqualTo(expectedNotification);
    }
}