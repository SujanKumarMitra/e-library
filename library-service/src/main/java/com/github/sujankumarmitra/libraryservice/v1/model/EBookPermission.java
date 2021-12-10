package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

public abstract class EBookPermission {

    public abstract String getBookId();

    public abstract String getUserId();

    public abstract Long getStartTimeInEpochMilliseconds();

    public abstract Long getDurationInMilliseconds();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBookPermission)) return false;

        EBookPermission that = (EBookPermission) o;

        return Objects.equals(getBookId(), that.getBookId()) &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getStartTimeInEpochMilliseconds(), that.getStartTimeInEpochMilliseconds()) &&
                Objects.equals(getDurationInMilliseconds(), that.getDurationInMilliseconds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookId(),
                getUserId(),
                getStartTimeInEpochMilliseconds(),
                getDurationInMilliseconds());
    }
}