package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.BookType.PHYSICAL;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreatePhysicalBookRequest extends JacksonCreateBookRequest {
    @NotNull
    @PositiveOrZero
    private Long copiesAvailable;
    @NotNull
    private JacksonMoney finePerDay;

    @Override
    public BookType getType() {
        return PHYSICAL;
    }
}
