package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;

import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class MoneySchema extends Money {
    @Override
    public BigDecimal getAmount() {
        return null;
    }

    @Override
    public String getCurrencyCode() {
        return null;
    }
}
