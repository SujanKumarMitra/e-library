package com.github.sujankumarmitra.assetservice.v1.exception;

import java.util.Collection;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class ApiOperationException extends RuntimeException {
    public abstract <E extends ErrorDetails> Collection<E> getErrors();
}
