package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Getter
@AllArgsConstructor
public final class LeaseRequestNotFoundException extends ApiOperationException {
    @NonNull
    private final Collection<ErrorDetails> errors;

    public LeaseRequestNotFoundException(String leaseRequestId) {
        this.errors = List.of(
                new DefaultErrorDetails(
                        "lease request not found with id '" + leaseRequestId + "'"));
    }
}
