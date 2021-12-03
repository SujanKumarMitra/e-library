package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import lombok.Getter;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class JacksonCreateBookTagRequestAdaptor extends BookTag {
    private final JacksonCreateBookTagRequest request;

    public JacksonCreateBookTagRequestAdaptor(JacksonCreateBookTagRequest request) {
        this.request = request;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getBookId() {
        return null;
    }

    @Override
    public String getKey() {
        return this.request.getKey();
    }

    @Override
    public String getValue() {
        return this.request.getValue();
    }
}
