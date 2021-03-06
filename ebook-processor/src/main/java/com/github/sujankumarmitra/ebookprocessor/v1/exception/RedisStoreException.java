package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@AllArgsConstructor
public final class RedisStoreException extends ApiOperationException {
    @NonNull
    private final Collection<ErrorDetails> errors;

    public RedisStoreException(String message) {
        this(List.of(new DefaultErrorDetails(message)));

    }
}
