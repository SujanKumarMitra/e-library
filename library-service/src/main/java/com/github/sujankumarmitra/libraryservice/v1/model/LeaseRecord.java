package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class LeaseRecord {

    public abstract String getLeaseRequestId();

    public abstract Long getStartTime();

    public abstract Long getEndTime();

    public abstract Boolean isRelinquished();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaseRecord)) return false;

        LeaseRecord that = (LeaseRecord) o;
        return Objects.equals(getLeaseRequestId(), that.getLeaseRequestId()) &&
                Objects.equals(getStartTime(), that.getStartTime()) &&
                Objects.equals(getEndTime(), that.getEndTime()) &&
                Objects.equals(isRelinquished(), that.isRelinquished());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getLeaseRequestId(),
                getStartTime(),
                getEndTime(),
                isRelinquished());
    }

    @Override
    public String toString() {
        return "LeaseRecord{" +
                "leaseRequestId='" + getLeaseRequestId() + '\'' +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", relinquished=" + isRelinquished() +
                '}';
    }

}
