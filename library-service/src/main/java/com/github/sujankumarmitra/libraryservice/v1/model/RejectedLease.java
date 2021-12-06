package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class RejectedLease {

    public abstract String getLeaseRequestId();

    public abstract String getReasonPhrase();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejectedLease)) return false;
        RejectedLease that = (RejectedLease) o;
        return Objects.equals(getLeaseRequestId(), that.getLeaseRequestId()) &&
                Objects.equals(getReasonPhrase(), that.getReasonPhrase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getLeaseRequestId(),
                getReasonPhrase());
    }

    @Override
    public String toString() {
        return "RejectedLease{" +
                "leaseRequestId='" + getLeaseRequestId() + '\'' +
                ", reasonPhrase='" + getReasonPhrase() + '\'' +
                '}';
    }
}