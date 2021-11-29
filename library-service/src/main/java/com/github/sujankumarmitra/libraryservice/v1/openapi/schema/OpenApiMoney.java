package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "Money")
public class OpenApiMoney extends Money {
    @Override
    public BigDecimal getAmount() {
        return null;
    }

    @Override
    public String getCurrencyCode() {
        return null;
    }
}
