package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public final class InsufficientCopiesAvailableException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public InsufficientCopiesAvailableException(String bookId) {
        this.errors = List.of(
                new DefaultErrorDetails("Insufficient copies available for physical book with bookId '" + bookId + "'")
        );
    }
}
