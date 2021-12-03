package com.github.sujankumarmitra.libraryservice.v1.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Money {

    public abstract BigDecimal getAmount();

    public abstract String getCurrencyCode();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(getAmount(), money.getAmount())
                && Objects.equals(getCurrencyCode(), money.getCurrencyCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getCurrencyCode());
    }

    @Override
    public String toString() {
        return "Money{" +
                "amount=" + getAmount() +
                ", currencyCode='" + getCurrencyCode() + '\'' +
                '}';
    }

}
