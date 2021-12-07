package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import lombok.AllArgsConstructor;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@AllArgsConstructor
public class JacksonValidRejectLeaseRequestRequestAdaptor extends RejectedLease {
    private final JacksonValidRejectLeaseRequestRequest request;

    @Override
    public String getLeaseRequestId() {
        return request.getLeaseRequestId();
    }

    @Override
    public String getReasonPhrase() {
        return request.getReasonPhrase();
    }
}
