package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class LibrarianAlreadyExistsException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public LibrarianAlreadyExistsException(String librarianId) {
        this(List.of(new DefaultErrorDetails("librarian exists with id '" + librarianId + "'")));
    }
}
