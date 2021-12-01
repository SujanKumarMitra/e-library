package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create an author of a book")
public class CreateAuthorRequestSchema extends Author {
    @Schema(hidden = true)
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
    @Schema(description = "name of author. name must be unique")
    @NotEmpty
    public String getName() {
        return null;
    }
}
