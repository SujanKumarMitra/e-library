package com.github.sujankumarmitra.notificationservice.v1.exception;

import java.io.Serializable;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class ErrorDetails implements Serializable {
    public abstract String getMessage();
}