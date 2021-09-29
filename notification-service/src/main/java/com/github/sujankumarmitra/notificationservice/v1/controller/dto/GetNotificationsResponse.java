package com.github.sujankumarmitra.notificationservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Data
@JsonPropertyOrder({"count", "notifications"})
public class GetNotificationsResponse {
    @NonNull
    @Schema(required = true)
    private final List<NotificationDto> notifications;

    @Schema(required = true, title = "total count of notifications")
    public int getCount() {
        return notifications.size();
    }

    public GetNotificationsResponse(@NonNull List<? extends Notification> notifications) {
        this.notifications = notifications
                .stream()
                .map(NotificationDto::new)
                .collect(toUnmodifiableList());
    }
}
