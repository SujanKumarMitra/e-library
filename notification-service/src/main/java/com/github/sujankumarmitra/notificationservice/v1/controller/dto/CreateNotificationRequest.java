package com.github.sujankumarmitra.notificationservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Schema(name = "CreateNotificationRequestSchema")
public class CreateNotificationRequest extends Notification {

    @NotNull
    @Schema(description = "the timestamp when the notification information is created." +
            " Represented in UNIX epoch milliseconds")
    private long createdAt;
    @NotBlank
    @Schema(description = "the consumer of this notification." +
            "This value corresponds to JWT sub claim")
    private String consumerId;
    @NotBlank
    @Schema(description = "useful information related to this notification." +
            "Never null or empty. use \"{}\" rather")
    private String payload;


    @JsonIgnore
    public String getId() {
        return null;
    }

    @JsonIgnore
    public boolean isAcknowledged() {
        return false;
    }

    @Override
    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
