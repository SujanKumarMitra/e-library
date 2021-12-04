package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.PositiveOrZero;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.PHYSICAL;
import static java.math.BigDecimal.ZERO;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidUpdatePhysicalBookRequest extends JacksonValidUpdateBookRequest {
    @PositiveOrZero
    private Long copiesAvailable;
    private JacksonValidMoney finePerDay;

    @Override
    public JacksonBookType getType() {
        return PHYSICAL;
    }


    @AssertTrue(message = "amount must be zero or positive")
    public boolean isValidFinePerDayUpdate() {
        if (finePerDay == null) return true;
        if (finePerDay.getAmount() == null) return true;

        return finePerDay.getAmount().compareTo(ZERO) >= 0;
    }

    @AssertTrue(message = "currency code length must be 3")
    public boolean isValidCurrencyCode() {
        if (finePerDay == null) return true;
        if (finePerDay.getCurrencyCode() == null) return true;

        return finePerDay.getCurrencyCode().length() == 3;
    }

}
