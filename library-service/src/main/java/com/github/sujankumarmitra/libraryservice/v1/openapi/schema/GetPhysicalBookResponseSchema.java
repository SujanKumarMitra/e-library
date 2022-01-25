package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@Schema(description = "Payload for physical book. See type")
public class GetPhysicalBookResponseSchema extends PhysicalBook {
    @Override
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getLibraryId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getTitle() {
        return null;
    }

    @Override
    @NotEmpty
    @SuppressWarnings("unchecked")
    public Set<GetAuthorResponseSchema> getAuthors() {
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
    @Schema(description = "the cover page image asset id. Can be null", nullable = true)
    public String getCoverPageImageAssetId() {
        return null;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public Set<GetBookTagResponseSchema> getTags() {
        return Collections.emptySet();
    }

    @Override
    @Schema(description = "total no of copies available in library." +
            "<br> lease requests will not be made if it's value is 0")
    public Long getCopiesAvailable() {
        return null;
    }

    @Schema(
            description = "The value will always set to 'PHYSICAL_BOOK'",
            implementation = String.class,
            allowableValues = {"PHYSICAL_BOOK"})
    public BookTypeSchema getType() {
        return null;
    }

    @Override
    @NotNull
    public MoneySchema getFinePerDay() {
        return new MoneySchema();
    }
}
