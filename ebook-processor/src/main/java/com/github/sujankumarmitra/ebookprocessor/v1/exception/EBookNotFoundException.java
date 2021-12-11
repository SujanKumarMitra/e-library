package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class EBookNotFoundException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public EBookNotFoundException(String eBookId) {
        this(List.of(new DefaultErrorDetails("ebook with id '" + eBookId + "' not found")));
    }
}
