package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class AcceptedLease {

    public abstract String getLeaseRequestId();

    public abstract Long getStartTime();

    public abstract Long getEndTime();

    @Override
    public int hashCode() {
        return Objects.hash(
                getLeaseRequestId(),
                getStartTime(),
                getEndTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AcceptedLease)) return false;

        AcceptedLease other = (AcceptedLease) obj;
        return Objects.equals(getLeaseRequestId(), other.getLeaseRequestId()) &&
                Objects.equals(getStartTime(), other.getStartTime()) &&
                Objects.equals(getEndTime(), other.getEndTime());
    }

    @Override
    public String toString() {
        return "AcceptedLease{" +
                "leaseRequestId='" + getLeaseRequestId() + '\'' +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                "}";
    }
}
