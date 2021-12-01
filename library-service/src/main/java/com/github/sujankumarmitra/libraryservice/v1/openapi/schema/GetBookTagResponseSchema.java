package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetBookTagResponseSchema extends BookTag {
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
    public String getKey() {
        return null;
    }

    @Override
    @NotEmpty
    public String getValue() {
        return null;
    }
}
