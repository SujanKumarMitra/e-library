package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Notification {

    public abstract String getConsumerId();

    public abstract String getPayload();

    public abstract Long getCreatedAt();

    @Override
    public String toString() {
        return "Notification{" +
                "consumerId='" + getConsumerId() + '\'' +
                ", payload='" + getPayload() + '\'' +
                ", timestamp=" + getCreatedAt() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(getConsumerId(), that.getConsumerId()) &&
                Objects.equals(getPayload(), that.getPayload()) &&
                Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getConsumerId(),
                getPayload(),
                getCreatedAt());
    }

}
