package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class EBookSegmentAssetNotFoundException extends ApiOperationException {
    private final Collection<ErrorDetails> errors;

    public EBookSegmentAssetNotFoundException(String assetId, String segmentId) {
        this(List.of(new DefaultErrorDetails("asset with id '" + assetId + "' not found for ebook segment with id '" + segmentId + "'")));
    }
}
