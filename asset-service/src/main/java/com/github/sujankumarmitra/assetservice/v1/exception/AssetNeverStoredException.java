package com.github.sujankumarmitra.assetservice.v1.exception;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Getter
public class AssetNeverStoredException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public AssetNeverStoredException(String assetId) {
        errors = List.of(new DefaultErrorDetails("asset never stored for assetId '{" + assetId + "}"));
    }
}
