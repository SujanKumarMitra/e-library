package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.Getter;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Getter
public final class InternalError extends RuntimeException {
    private final String message;

    public InternalError(String message) {
        this.message = message;
    }

}
