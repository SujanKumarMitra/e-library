package com.github.sujankumarmitra.libraryservice.v1.model;

import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Money {

    public abstract BigDecimal getAmount();

    public abstract String getCurrencyCode();

}
