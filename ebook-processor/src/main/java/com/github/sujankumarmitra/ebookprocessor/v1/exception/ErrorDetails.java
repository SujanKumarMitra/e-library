package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import java.io.Serializable;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@SuppressWarnings("ClassMayBeInterface")
public abstract class ErrorDetails implements Serializable {
    @SuppressWarnings("unused")
    public abstract String getMessage();
}
