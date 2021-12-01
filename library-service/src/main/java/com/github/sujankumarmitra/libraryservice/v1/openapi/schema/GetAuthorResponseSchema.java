package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetAuthorResponseSchema extends Author {
    @Override
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getBookId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getName() {
        return null;
    }
}
