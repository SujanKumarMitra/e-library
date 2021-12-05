package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 25/11/21, 2021
 */
@Getter
public final class DuplicateAuthorNameException extends ApiOperationException {

    @NonNull
    private final Collection<ErrorDetails> errors;

    public DuplicateAuthorNameException(String authorName) {
        this.errors = List.of(new DefaultErrorDetails("authorName '" + authorName + "' already exists"));
    }

    public DuplicateAuthorNameException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }

}
