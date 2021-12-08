package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class AcceptedLease {

    public static final Long INFINITE_LEASE_DURATION = -1L;

    public abstract String getLeaseRequestId();

    public abstract Long getStartTimeInEpochMilliseconds();

    public abstract Long getDurationInMilliseconds();

    @Override
    public int hashCode() {
        return Objects.hash(
                getLeaseRequestId(),
                getStartTimeInEpochMilliseconds(),
                getDurationInMilliseconds());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AcceptedLease)) return false;

        AcceptedLease other = (AcceptedLease) obj;
        return Objects.equals(getLeaseRequestId(), other.getLeaseRequestId()) &&
                Objects.equals(getStartTimeInEpochMilliseconds(), other.getStartTimeInEpochMilliseconds()) &&
                Objects.equals(getDurationInMilliseconds(), other.getDurationInMilliseconds());
    }

    @Override
    public String toString() {
        return "AcceptedLease{" +
                "leaseRequestId='" + getLeaseRequestId() + '\'' +
                ", startTime=" + getStartTimeInEpochMilliseconds() +
                ", endTime=" + getDurationInMilliseconds() +
                "}";
    }
}
