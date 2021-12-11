package com.github.sujankumarmitra.ebookprocessor.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class EBookProcessingStatus {

    public abstract String getProcessId();

    public abstract ProcessingState getState();

    public abstract String getMessage();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBookProcessingStatus)) return false;

        EBookProcessingStatus that = (EBookProcessingStatus) o;
        return Objects.equals(getProcessId(), that.getProcessId()) &&
                getState() == that.getState() &&
                Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProcessId(),
                getState(),
                getMessage());
    }

}
