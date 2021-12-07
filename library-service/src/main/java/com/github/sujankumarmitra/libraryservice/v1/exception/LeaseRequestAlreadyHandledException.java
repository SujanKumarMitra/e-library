package com.github.sujankumarmitra.libraryservice.v1.exception;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
public class LeaseRequestAlreadyHandledException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public LeaseRequestAlreadyHandledException(String leaseRequestId, LeaseStatus status) {
        this(List.of(
                new DefaultErrorDetails("lease request with id '" + leaseRequestId + "' is already set to '" + status + "'")));
    }

    public LeaseRequestAlreadyHandledException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }
}
