package com.github.sujankumarmitra.libraryservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 25/11/21, 2021
 */
public final class DuplicateAuthorNameException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public DuplicateAuthorNameException(String authorName) {
        this.errors = List.of(new DefaultErrorDetails("authorName '" + authorName + "' already exists"));
    }

    public DuplicateAuthorNameException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
