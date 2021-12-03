package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public class JacksonUpdateAuthorRequestAdaptor extends Author {
    private final JacksonUpdateAuthorRequest request;

    public JacksonUpdateAuthorRequestAdaptor(@NonNull JacksonUpdateAuthorRequest request) {
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
    public String getName() {
        return request.getName();
    }
}
