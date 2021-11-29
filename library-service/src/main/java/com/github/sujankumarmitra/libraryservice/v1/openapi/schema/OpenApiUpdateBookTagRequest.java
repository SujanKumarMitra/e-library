package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.*;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "UpdateBookTagRequest")
public class OpenApiUpdateBookTagRequest extends BookTag {
    @Override
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
