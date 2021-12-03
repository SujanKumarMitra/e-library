package com.github.sujankumarmitra.libraryservice.v1.exception;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
public class NegativeMoneyAmountException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    @NonNull
    public NegativeMoneyAmountException(BigDecimal amount) {
        errors = List.of(
                new DefaultErrorDetails("amount can't be negative: " + amount)
        );
    }


    @Override
    public Collection<ErrorDetails> getErrors() {
        return errors;
    }
}
