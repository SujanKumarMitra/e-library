package com.github.sujankumarmitra.libraryservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
public final class BookNotFoundException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public BookNotFoundException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }

    public BookNotFoundException(String bookId) {
        this(List.of(new DefaultErrorDetails("book with id '" + bookId + "' not found")));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
