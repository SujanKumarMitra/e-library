package com.github.sujankumarmitra.assetservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public class AssetNotFoundException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public AssetNotFoundException(String assetId) {
        errors = List.of(new DefaultErrorDetails("asset with id {" + assetId + "} not found"));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return errors.toString();
    }
}
