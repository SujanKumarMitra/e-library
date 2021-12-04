package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import lombok.Getter;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.EBOOK;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class JacksonValidUpdateEBookRequest extends JacksonValidUpdateBookRequest {
    private EBookFormat format;

    @Override
    public JacksonBookType getType() {
        return EBOOK;
    }
}
