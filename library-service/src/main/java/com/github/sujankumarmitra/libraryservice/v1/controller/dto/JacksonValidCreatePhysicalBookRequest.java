package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.PHYSICAL;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidCreatePhysicalBookRequest extends JacksonValidCreateBookRequest {
    @NotNull
    @PositiveOrZero
    private Long copiesAvailable;
    @NotNull
    @Valid
    private JacksonValidMoney finePerDay;

    @Override
    public JacksonBookType getType() {
        return PHYSICAL;
    }
}
