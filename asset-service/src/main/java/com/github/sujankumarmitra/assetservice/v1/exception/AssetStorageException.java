package com.github.sujankumarmitra.assetservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public class AssetStorageException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public AssetStorageException(Exception ex) {
        errors = List.of(new DefaultErrorDetails(ex.getMessage()));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
