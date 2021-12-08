package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
public class DefaultMoney extends Money {
    private final BigDecimal amount;
    private final String currencyCode;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
