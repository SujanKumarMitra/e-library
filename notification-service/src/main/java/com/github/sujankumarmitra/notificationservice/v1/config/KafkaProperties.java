package com.github.sujankumarmitra.notificationservice.v1.config;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
public abstract class KafkaProperties {

    public abstract String getBootstrapServers();

    public abstract String getNewNotificationsTopicName();

    public abstract String getCreateNotificationsTopicName();
}
