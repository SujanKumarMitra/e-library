package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@AllArgsConstructor
public class JacksonUpdateAuthorRequestAdaptor extends Author {
    @NonNull
    private final JacksonUpdateAuthorRequest request;

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
