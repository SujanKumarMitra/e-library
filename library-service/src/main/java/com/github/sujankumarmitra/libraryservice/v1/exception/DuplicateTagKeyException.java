package com.github.sujankumarmitra.libraryservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 25/11/21, 2021
 */
public final class DuplicateTagKeyException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public DuplicateTagKeyException(String s) {
        this.errors = List.of(new DefaultErrorDetails(s));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
