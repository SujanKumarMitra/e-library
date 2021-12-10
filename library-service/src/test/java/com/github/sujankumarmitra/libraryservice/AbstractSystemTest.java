package com.github.sujankumarmitra.libraryservice;

import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import com.github.sujankumarmitra.libraryservice.v1.config.KafkaTestConfiguration;
import com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.ConnectionAccessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@DirtiesContext
@Import(KafkaTestConfiguration.class)
public abstract class AbstractSystemTest {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    @Container
    protected static final KafkaContainer KAFKA_CONTAINER;

    public static final String NOTIFICATIONS_TOPIC_NAME = "new_notifications";

    @Autowired
    protected ConnectionAccessor connectionAccessor;
    @Autowired
    protected KafkaProperties kafkaProperties;
    @Autowired
    protected AdminClient adminClient;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres");
        KAFKA_CONTAINER = new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-server:6.2.0")
                        .asCompatibleSubstituteFor("confluentinc/cp-kafka"));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> POSTGRESQL_CONTAINER.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.r2dbc.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("app.kafka.bootstrapServers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("app.kafka.notificationsTopicName", () -> NOTIFICATIONS_TOPIC_NAME);
    }

    @BeforeEach
    void createKafkaTopics() {
        try {
            adminClient
                    .createTopics(asList(new NewTopic(
                            kafkaProperties.getNotificationsTopicName(),
                            empty(),
                            empty())))
                    .all()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Error while creating kafka topics", e);
        }
    }

    @AfterEach
    void cleanupDatabase() {
        DaoTestUtils.truncateAllTables(connectionAccessor);
    }

    @AfterEach
    void deleteKafkaTopics() {
        adminClient.deleteTopics(Arrays.asList(kafkaProperties.getNotificationsTopicName()));
    }

}
