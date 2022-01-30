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

    public abstract Boolean isRelinquished();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcceptedLease)) return false;

        AcceptedLease that = (AcceptedLease) o;
        return Objects.equals(getLeaseRequestId(), that.getLeaseRequestId()) &&
                Objects.equals(getStartTimeInEpochMilliseconds(), that.getStartTimeInEpochMilliseconds()) &&
                Objects.equals(getDurationInMilliseconds(), that.getDurationInMilliseconds()) &&
                Objects.equals(isRelinquished(), that.isRelinquished());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getLeaseRequestId(),
                getStartTimeInEpochMilliseconds(),
                getDurationInMilliseconds(),
                isRelinquished());
    }

    @Override
    public String toString() {
        return "LeaseRecord{" +
                "leaseRequestId='" + getLeaseRequestId() + '\'' +
                ", startTime=" + getStartTimeInEpochMilliseconds() +
                ", endTime=" + getDurationInMilliseconds() +
                ", relinquished=" + isRelinquished() +
                '}';
    }

}
