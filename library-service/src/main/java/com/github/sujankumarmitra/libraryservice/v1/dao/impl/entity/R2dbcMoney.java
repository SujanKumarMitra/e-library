package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
@NoArgsConstructor
public final class R2dbcMoney extends Money {
    private BigDecimal amount;
    private String currencyCode;

    public R2dbcMoney(@NonNull Money money) {
        this.amount = money.getAmount();
        this.currencyCode = money.getCurrencyCode();
    }
}
