package com.github.sujankumarmitra.notificationservice.v1.service;

import com.github.sujankumarmitra.notificationservice.v1.config.KafkaProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

import static java.util.Optional.empty;

/**
 * Creates Kafka topics for notifications if not created already
 *
 * @author skmitra
 * @since Nov 12/11/21, 2021
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaTopicCreator implements InitializingBean {

    private final KafkaProperties kafkaProperties;

    private void logResult(Void __, Throwable th) {
        if (th != null) log.warn("Failed to create notification topic", th);
        else log.info("Notification topics created");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties adminClientProps = new Properties();
        adminClientProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getBootstrapServers());

        AdminClient adminClient = AdminClient.create(adminClientProps);
        NewTopic newNotificationsTopic = new NewTopic(
                kafkaProperties.getNewNotificationsTopicName(),
                empty(), empty());

        NewTopic createNotificationsTopic = new NewTopic(
                kafkaProperties.getCreateNotificationsTopicName(),
                empty(), empty());

        adminClient.createTopics(List.of(newNotificationsTopic, createNotificationsTopic))
                .all()
                .whenComplete(this::logResult);

        adminClient.close();
    }
}
