package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class DuplicateEBookSegmentIndexException extends ApiOperationException {
    @NonNull
    private final Collection<ErrorDetails> errors;

    public DuplicateEBookSegmentIndexException(String ebookId, int index) {
        this(List.of(new DefaultErrorDetails("segment with index " + index + " already exists for bookId '" + ebookId + "'")));
    }
}
