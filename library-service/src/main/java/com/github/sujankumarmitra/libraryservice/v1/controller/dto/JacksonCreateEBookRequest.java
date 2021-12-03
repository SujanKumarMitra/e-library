package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import lombok.Getter;

import javax.validation.constraints.NotNull;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.BookType.EBOOK;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class JacksonCreateEBookRequest extends JacksonCreateBookRequest {
    @NotNull
    private EBookFormat format;

    @Override
    public BookType getType() {
        return EBOOK;
    }
}
