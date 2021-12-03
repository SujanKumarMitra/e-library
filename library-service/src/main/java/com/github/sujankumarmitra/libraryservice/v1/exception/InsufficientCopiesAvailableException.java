package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class InsufficientCopiesAvailableException extends ApiOperationException {


    private final Collection<ErrorDetails> errors;

    public InsufficientCopiesAvailableException(long copiesAvailable) {
        this.errors = List.of(
                new DefaultErrorDetails("Insufficient copies available = " + copiesAvailable)
        );
    }
}
