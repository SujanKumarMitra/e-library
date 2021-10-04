package com.github.sujankumarmitra.notificationservice.v1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@Data
public class JacksonNewNotificationEvent extends NewNotificationEvent {
    private final String notificationId;
    private final String consumerId;

    @JsonCreator
    public JacksonNewNotificationEvent(@JsonProperty("notificationId") String notificationId, @JsonProperty(value = "consumerId") String consumerId) {
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
