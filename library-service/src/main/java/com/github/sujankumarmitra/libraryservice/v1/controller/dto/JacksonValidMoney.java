package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidMoney extends Money {
    @NotNull
    @PositiveOrZero(message = "amount must be positive")
    private BigDecimal amount;

    @NotNull
    @Size(min = 3, max = 3, message = "currency code length must be 3")
    private String currencyCode;


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
