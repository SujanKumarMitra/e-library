package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.AllArgsConstructor;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@AllArgsConstructor
public class JacksonGetPendingLeaseRequestResponse extends LeaseRequest {
    private final LeaseRequest delegate;

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getLibraryId() {
        return delegate.getLibraryId();
    }

    @Override
    public String getBookId() {
        return delegate.getBookId();
    }

    @Override
    public String getUserId() {
        return delegate.getUserId();
    }

    @Override
    @JsonIgnore
    public LeaseStatus getStatus() {
        return delegate.getStatus();
    }

    @Override
    public Long getTimestamp() {
        return delegate.getTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
