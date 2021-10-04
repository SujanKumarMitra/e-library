package com.github.sujankumarmitra.notificationservice.v1.dao;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.notificationservice.v1.exception.NotificationNotFoundException;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@DataMongoTest
@Slf4j
class MongoNotificationDaoTest {

    public static final String VALID_CONSUMER_ID = "VALID_CONSUMER_ID";
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    private MongoNotificationDao daoUnderTest;

    @BeforeEach
    void setUp() {
        daoUnderTest = new MongoNotificationDao(mongoTemplate);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(MongoNotificationDocument.class).all().block();
    }

    @Test
    void testInsertValidNotification() {
        MongoNotificationDocument notificationDocument = MongoNotificationDocument
                .newBuilder()
                .createdAt(currentTimeMillis())
                .consumerId("VALID_CONSUMER_ID")
                .payload("VALID_PAYLOAD")
                .acknowledged(false)
                .build();

        String createdNotificationId = daoUnderTest.insert(notificationDocument).block();

        assertThat(createdNotificationId)
                .isNotNull()
                .isNotEmpty()
                .isNotBlank();

        log.info("Created Document Id {}", createdNotificationId);
    }


    @Test
    void given10NotificationsForA_Consumer_whenFindWithLimit20_shouldFetch5InDescOrder() {
        String consumerId = new Faker().idNumber().valid();
        Stream<String> notificationStream = Stream.generate(() -> consumerId);

        Flux.fromStream(notificationStream)
                .delayElements(Duration.ofMillis(200))
                .take(10)
                .map(cId -> MongoNotificationDocument
                        .newBuilder()
                        .createdAt(nanoTime())
                        .consumerId(cId)
                        .payload("VALID_PAYLOAD")
                        .acknowledged(false)
                        .build())
                .flatMap(mongoTemplate::insert)
                .blockLast();

        List<Notification> foundNotifications = daoUnderTest
                .find(consumerId, 20)
                .collectList()
                .block();

        log.info("Found {} notifications", foundNotifications.size());
        assertThat(foundNotifications).hasSize(10);

        foundNotifications
                .stream()
                .map(Notification::getConsumerId)
                .forEach(cId -> assertThat(cId).isEqualTo(consumerId));

        long previousAt = foundNotifications.get(0).getCreatedAt();
        for (int i = 1; i < foundNotifications.size(); i++) {
            long currentAt = foundNotifications.get(i).getCreatedAt();
            log.info("Previous timestamp {}, Current timestamp {}", previousAt, currentAt);
            assertThat(currentAt).isLessThanOrEqualTo(previousAt);
            previousAt = currentAt;
        }
    }


    @Test
    void givenValidNotificationIdAndConsumerId_whenSetAcknowledged_shouldSetAcknowledgement() {
        MongoNotificationDocument notificationDocument = MongoNotificationDocument
                .newBuilder()
                .createdAt(currentTimeMillis())
                .consumerId(VALID_CONSUMER_ID)
                .acknowledged(false)
                .payload("VALID_PAYLOAD")
                .build();

        String notificationId = mongoTemplate
                .insert(notificationDocument)
                .block()
                .getId();

        daoUnderTest.setAcknowledged(notificationId, VALID_CONSUMER_ID).block();

        MongoNotificationDocument fetchedNotificationDocument = mongoTemplate.findAll(MongoNotificationDocument.class)
                .take(1)
                .next()
                .block();

        assertThat(fetchedNotificationDocument.isAcknowledged()).isTrue();
    }

    @Test
    void givenValidNotificationIdButDifferentConsumerId_whenSetAcknowledged_shouldThrowException() {
        MongoNotificationDocument notificationDocument = MongoNotificationDocument
                .newBuilder()
                .createdAt(currentTimeMillis())
                .consumerId(VALID_CONSUMER_ID)
                .acknowledged(false)
                .payload("VALID_PAYLOAD")
                .build();

        String notificationId = mongoTemplate
                .insert(notificationDocument)
                .block()
                .getId();

        Assertions.assertThrows(NotificationNotFoundException.class,
                () -> daoUnderTest.setAcknowledged(notificationId, "INVALID_CONSUMER_ID").block());


    }

    @Test
    void givenValidNotificationIdAndConsumerId_whenFindOne_shouldFetchNotification() {
        MongoNotificationDocument notificationDocument = MongoNotificationDocument
                .newBuilder()
                .createdAt(currentTimeMillis())
                .consumerId(VALID_CONSUMER_ID)
                .payload("VALID_PAYLOAD")
                .acknowledged(false)
                .build();

        MongoNotificationDocument document = mongoTemplate.insert(notificationDocument).block();

        Notification fetchedNotification = daoUnderTest.findOne(document.getId(), VALID_CONSUMER_ID).block();

        assertThat(fetchedNotification).isNotNull();
        System.out.println(fetchedNotification);


    }

    @Test
    void givenInValidNotificationId_whenFindOne_shouldCompleteEmpty() {
        Notification fetchedNotification = daoUnderTest.findOne(ObjectId.get().toHexString(), VALID_CONSUMER_ID).block();

        assertThat(fetchedNotification).isNull();
        System.out.println(fetchedNotification);


    }

    @Test
    void givenValidNotificationIdButDifferentConsumerId_whenFindOne_shouldCompleteEmpty() {

        MongoNotificationDocument notificationDocument = MongoNotificationDocument
                .newBuilder()
                .createdAt(currentTimeMillis())
                .consumerId(VALID_CONSUMER_ID)
                .payload("VALID_PAYLOAD")
                .acknowledged(false)
                .build();

        MongoNotificationDocument document = mongoTemplate.insert(notificationDocument).block();

        Notification fetchedNotification = daoUnderTest.findOne(document.getId(), "INVALID_CONSUMER_ID").block();

        assertThat(fetchedNotification).isNull();
        System.out.println(fetchedNotification);


    }

}