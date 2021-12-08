package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class LeaseRecord {

    public abstract String getLeaseRequestId();

    public abstract Long getStartTimeInEpochMilliseconds();

    public abstract Long getDurationInMilliseconds();

    public abstract Boolean isRelinquished();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaseRecord)) return false;

        LeaseRecord that = (LeaseRecord) o;
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
