package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
public class JacksonCreateAuthorRequestAdaptor extends Author {
    @NotNull
    private final JacksonCreateAuthorRequest request;

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
