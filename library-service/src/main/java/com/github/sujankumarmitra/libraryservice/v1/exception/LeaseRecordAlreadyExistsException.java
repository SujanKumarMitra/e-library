package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
@AllArgsConstructor
public final class LeaseRecordAlreadyExistsException extends ApiOperationException {
    @NonNull
    private final Collection<ErrorDetails> errors;

    public LeaseRecordAlreadyExistsException(String leaseRequestId) {
        this(List.of(new DefaultErrorDetails("Lease Record already exists for leaseRequestId '" + leaseRequestId + "'")));
    }

}
