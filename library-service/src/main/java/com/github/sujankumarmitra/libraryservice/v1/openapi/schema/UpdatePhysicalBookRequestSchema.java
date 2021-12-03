package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to update a physical book." +
        "<br> if a field is missing or set to null, it's value will remain unchanged." +
        "<br> if an array field is null, it will remain unchanged, <b> but if it is empty, or filled, " +
        "all it's previous values will be replaced by new array")
public class UpdatePhysicalBookRequestSchema extends PhysicalBook {


    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<UpdateBookAuthorRequestSchema> getAuthors() {
        return Collections.emptySet();
    }

    @Override
    public String getPublisher() {
        return null;
    }

    @Override
    public String getEdition() {
        return null;
    }

    @Override
    public String getCoverPageImageAssetId() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<UpdateBookTagRequestSchema> getTags() {
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
    public Long getCopiesAvailable() {
        return null;
    }

    @Override
    public MoneySchema getFinePerDay() {
        return null;
    }
}
