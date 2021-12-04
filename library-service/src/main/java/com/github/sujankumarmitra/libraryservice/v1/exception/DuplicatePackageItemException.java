package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.Getter;

import java.util.Collection;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
public class DuplicatePackageItemException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public DuplicatePackageItemException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }
}
