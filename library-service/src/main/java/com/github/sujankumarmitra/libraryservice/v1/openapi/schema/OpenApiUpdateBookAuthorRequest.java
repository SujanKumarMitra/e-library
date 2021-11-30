package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "UpdateBookAuthorRequest")
public class OpenApiUpdateBookAuthorRequest extends Author {
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
    public String getName() {
        return null;
    }
}
