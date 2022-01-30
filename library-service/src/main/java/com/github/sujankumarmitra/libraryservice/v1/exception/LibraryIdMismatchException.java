package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Jan 26/01/22, 2022
 */
@Getter
@AllArgsConstructor
public class LibraryIdMismatchException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public LibraryIdMismatchException(String message) {
        errors = List.of(new DefaultErrorDetails(message));
    }
}
