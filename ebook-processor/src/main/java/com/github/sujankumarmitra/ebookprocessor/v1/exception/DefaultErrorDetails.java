package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import lombok.Data;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Data
public class DefaultErrorDetails extends ErrorDetails {
    @NonNull
    private final String message;
}
