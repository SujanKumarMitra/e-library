package com.github.sujankumarmitra.notificationservice.v1.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Data
@Builder(builderMethodName = "newBuilder")
public class DefaultNotification extends Notification {
    @NonNull
    private final String id;
    private final long createdAt;
    @NonNull
    private final String consumerId;
    @NonNull
    private final String payload;
    private final boolean acknowledged;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
