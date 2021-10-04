package com.github.sujankumarmitra.notificationservice.v1.model;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
public abstract class NewNotificationEvent {
    public abstract String getNotificationId();

    public abstract String getConsumerId();
}
