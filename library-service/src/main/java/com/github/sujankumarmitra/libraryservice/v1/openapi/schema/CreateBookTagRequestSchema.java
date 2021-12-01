package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create a book tag")
public class CreateBookTagRequestSchema extends BookTag {
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
    @NotEmpty
    @Schema(description = "the key of the tag. the key must be unique")
    public String getKey() {
        return null;
    }

    @Override
    @NotEmpty
    @Schema(description = "the value of the tag")
    public String getValue() {
        return null;
    }
}
