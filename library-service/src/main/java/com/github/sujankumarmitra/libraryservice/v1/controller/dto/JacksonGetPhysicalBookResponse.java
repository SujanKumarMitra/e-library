package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.NonNull;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.PHYSICAL;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
public class JacksonGetPhysicalBookResponse extends JacksonGetBookResponse {
    public Long getCopiesAvailable() {
        return book.getCopiesAvailable();
    }

    public Money getFinePerDay() {
        return book.getFinePerDay();
    }

    @NonNull
    private final PhysicalBook book;

    public JacksonGetPhysicalBookResponse(@NonNull PhysicalBook book) {
        super(book);
        this.book = book;
    }

    @Override
    public JacksonBookType getType() {
        return PHYSICAL;
    }
}
