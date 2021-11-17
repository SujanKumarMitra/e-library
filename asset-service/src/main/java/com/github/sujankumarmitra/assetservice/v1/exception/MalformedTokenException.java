package com.github.sujankumarmitra.assetservice.v1.exception;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Getter
public class MalformedTokenException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public MalformedTokenException(String tokenValue) {
        errors = List.of(new DefaultErrorDetails("Malformed token '" + tokenValue + "'"));
    }
}
