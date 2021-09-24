package com.github.sujankumarmitra.assetservice.v1.exception;

import lombok.Data;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Data
public class DefaultErrorDetails implements ErrorDetails {
    private final String message;
}
