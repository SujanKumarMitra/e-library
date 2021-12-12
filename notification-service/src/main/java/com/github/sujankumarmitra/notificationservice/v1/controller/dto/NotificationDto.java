package com.github.sujankumarmitra.notificationservice.v1.controller.dto;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Schema(name = "NotificationSchema")
public class NotificationDto extends Notification {

    public NotificationDto(Notification delegate) {
        this.delegate = delegate;
    }

    private final Notification delegate;

    @Schema(required = true)
    public String getId() {
        return this.delegate.getId();
    }

    @Schema(required = true)
    public long getCreatedAt() {
        return this.delegate.getCreatedAt();
    }

    @Schema(required = true)
    public String getConsumerId() {
        return this.delegate.getConsumerId();
    }

    @Schema(required = true)
    public String getPayload() {
        return this.delegate.getPayload();
    }

    @Schema(required = true)
    public boolean isAcknowledged() {
        return this.delegate.isAcknowledged();
    }
}
