package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class LeaseRequest {

    public abstract String getId();

    public abstract String getBookId();

    public abstract String getUserId();

    public abstract LeaseStatus getStatus();

    public abstract Long getTimestamp();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaseRequest)) return false;
        LeaseRequest that = (LeaseRequest) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getBookId(), that.getBookId()) &&
                Objects.equals(getUserId(), that.getUserId()) &&
                getStatus() == that.getStatus() &&
                Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBookId(), getUserId(), getStatus(), getTimestamp());
    }

    @Override
    public String toString() {
        return "LeaseRequest{" +
                "id=" + getId() +
                ", bookId=" + getBookId() +
                ", userId='" + getUserId() + '\'' +
                ", status=" + getStatus() +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
