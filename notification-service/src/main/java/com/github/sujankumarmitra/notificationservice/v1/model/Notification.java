package com.github.sujankumarmitra.notificationservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
public abstract class Notification {

    public abstract String getId();

    public abstract long getCreatedAt();

    public abstract String getConsumerId();

    public abstract String getPayload();

    public abstract boolean isAcknowledged();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return getCreatedAt() == that.getCreatedAt() && isAcknowledged() == that.isAcknowledged() && Objects.equals(getId(), that.getId()) && Objects.equals(getConsumerId(), that.getConsumerId()) && Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt(), getConsumerId(), getPayload(), isAcknowledged());
    }

}
