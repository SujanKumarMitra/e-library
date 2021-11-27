package com.github.sujankumarmitra.libraryservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
public final class PackageNotFoundException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public PackageNotFoundException(Collection<ErrorDetails> errors) {
        this.errors = errors;
    }

    public PackageNotFoundException(String packageId) {
        this(List.of(new DefaultErrorDetails("package with id + '" + packageId + "' not found")));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
