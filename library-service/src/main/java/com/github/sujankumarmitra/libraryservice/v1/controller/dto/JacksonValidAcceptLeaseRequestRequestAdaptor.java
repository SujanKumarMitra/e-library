package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import lombok.AllArgsConstructor;

import static java.lang.Boolean.FALSE;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@AllArgsConstructor
public class JacksonValidAcceptLeaseRequestRequestAdaptor extends AcceptedLease {
    private final JacksonValidAcceptLeaseRequestRequest request;

    @Override
    public String getLeaseRequestId() {
        return request.getLeaseRequestId();
    }

    @Override
    public Long getStartTimeInEpochMilliseconds() {
        return request.getStartTimeInEpochMilliseconds();
    }

    @Override
    public Long getDurationInMilliseconds() {
        return request.getDurationInMilliseconds();
    }

    @Override
    public Boolean isRelinquished() {
        return FALSE;
    }

}
