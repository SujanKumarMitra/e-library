package com.github.sujankumarmitra.notificationservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Data
@JsonPropertyOrder({"count", "notifications"})
public class GetNotificationsResponse {
    @NonNull
    private final List<Notification> notifications;

    public int getCount() {
        return notifications.size();
    }

}
