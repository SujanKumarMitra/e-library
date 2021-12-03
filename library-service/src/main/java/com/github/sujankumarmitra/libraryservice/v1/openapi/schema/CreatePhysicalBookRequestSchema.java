package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create a physical book")
public class CreatePhysicalBookRequestSchema extends PhysicalBook {
    @Schema(hidden = true)
    @Override
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getTitle() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Size(min = 1)
    public Set<CreateAuthorRequestSchema> getAuthors() {
        return Collections.emptySet();
    }

    @Override
    @NotEmpty
    public String getPublisher() {
        return null;
    }

    @Override
    @NotEmpty
    public String getEdition() {
        return null;
    }

    @Override
    @Schema(description = "the cover page image asset id")
    public String getCoverPageImageAssetId() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public Set<CreateBookTagRequestSchema> getTags() {
        return Collections.emptySet();
    }

    @Schema(
            implementation = String.class,
            allowableValues = {"PHYSICAL_BOOK"},
            description = "the value must be set to 'PHYSICAL_BOOK'"
    )
    @NotEmpty
    public BookTypeSchema getType() {
        return null;
    }

    @Override
    @NotEmpty
    @Min(0)
    public Long getCopiesAvailable() {
        return null;
    }

    @Override
    @NotEmpty
    public MoneySchema getFinePerDay() {
        return null;
    }
}
