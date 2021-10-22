package com.github.sujankumarmitra.notificationservice.v1.model;

import lombok.Data;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Data
public class DefaultNewNotificationEvent extends NewNotificationEvent {
    private final String notificationId;
    private final String consumerId;

    public DefaultNewNotificationEvent(String notificationId, String consumerId) {
        this.notificationId = notificationId;
        this.consumerId = consumerId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
