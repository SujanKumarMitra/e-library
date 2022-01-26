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
public class IncorrectLibraryIdException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public IncorrectLibraryIdException(String libraryId) {
        errors = List.of(new DefaultErrorDetails("'" + libraryId + "' does not belong to given package"));
    }
}
