package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreateAuthorRequestAdaptor extends Author {
    private final JacksonCreateAuthorRequest request;

    public JacksonCreateAuthorRequestAdaptor(@NotNull JacksonCreateAuthorRequest request) {
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
    public String getName() {
        return request.getName();
    }
}
