package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class MoneySchema extends Money {
    @Override
    @NotNull
    @NotEmpty
    @Schema(description = "the fine amount in real number")
    public BigDecimal getAmount() {
        return BigDecimal.ZERO;
    }

    @Override
    @NotEmpty
    @Schema(description = "the code of currency of amount", example = "INR")
    public String getCurrencyCode() {
        return null;
    }
}
