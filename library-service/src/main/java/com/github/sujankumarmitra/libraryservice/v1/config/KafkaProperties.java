package com.github.sujankumarmitra.libraryservice.v1.config;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
public abstract class KafkaProperties {

    public abstract String getBootstrapServers();

    public abstract String getNotificationsTopicName();

    public abstract String getCreateAssetPermissionsTopicName();
}
