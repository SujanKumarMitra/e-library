package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public class JacksonUpdateBookTagRequestAdaptor extends BookTag {
    private final JacksonUpdateBookTagRequest request;

    public JacksonUpdateBookTagRequestAdaptor(@NonNull JacksonUpdateBookTagRequest request) {
        this.request = request;
    }

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getBookId() {
        return request.getBookId();
    }

    @Override
    public String getKey() {
        return request.getKey();
    }

    @Override
    public String getValue() {
        return request.getValue();
    }

}