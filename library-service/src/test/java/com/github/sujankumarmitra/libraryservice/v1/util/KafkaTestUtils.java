package com.github.sujankumarmitra.libraryservice.v1.util;

import com.github.sujankumarmitra.libraryservice.v1.config.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;

import static java.util.Optional.empty;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Slf4j
public class KafkaTestUtils {

    private KafkaTestUtils() {
    }

    public static void createTopics(KafkaProperties properties, AdminClient client) {
        NewTopic topic1 = new NewTopic(properties.getNotificationsTopicName(), empty(), empty());
        NewTopic topic2 = new NewTopic(properties.getCreateAssetPermissionsTopicName(), empty(), empty());


        try {
            client.createTopics(List.of(topic1, topic2))
                    .all()
                    .get();
            log.info("created topics");
        } catch (Throwable th) {
            log.warn("Error", th);
        }
    }

    public static void deleteTopics(KafkaProperties properties, AdminClient client) {
        try {
            client.deleteTopics(List.of(
                            properties.getNotificationsTopicName(),
                            properties.getCreateAssetPermissionsTopicName()))
                    .all();

            log.info("deleted topics");
        }catch (Throwable th) {
            log.warn("Error ", th);

        }


    }
}
