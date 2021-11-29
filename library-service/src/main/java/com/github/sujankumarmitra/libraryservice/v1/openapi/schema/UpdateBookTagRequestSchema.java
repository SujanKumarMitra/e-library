package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class UpdateBookTagRequestSchema extends BookTag {
    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public String getBookId() {
        return null;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
