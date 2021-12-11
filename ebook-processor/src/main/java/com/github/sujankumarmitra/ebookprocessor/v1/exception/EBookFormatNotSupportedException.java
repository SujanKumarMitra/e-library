package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
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
public class EBookFormatNotSupportedException extends ApiOperationException {
    @NonNull
    private final Collection<ErrorDetails> errors;

    public EBookFormatNotSupportedException(EBookFormat format) {
        this(List.of(new DefaultErrorDetails("EBookFormat '" + format + "' is not supported")));
    }
}
