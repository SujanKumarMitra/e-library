package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import lombok.NonNull;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.EBOOK;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
public class JacksonGetEBookResponse extends JacksonGetBookResponse {

    @NonNull
    private final EBook book;

    public JacksonGetEBookResponse(@NonNull EBook book) {
        super(book);
        this.book = book;
    }

    @Override
    public JacksonBookType getType() {
        return EBOOK;
    }

    public EBookFormat getFormat() {
        return book.getFormat();
    }
}
