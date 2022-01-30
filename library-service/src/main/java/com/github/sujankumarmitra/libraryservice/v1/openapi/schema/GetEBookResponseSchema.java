package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetEBookResponseSchema extends EBook {
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
    @SuppressWarnings("unchecked")
    @NotEmpty
    public Set<GetBookAuthorResponseSchema> getAuthors() {
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
    public String getCoverPageImageAssetId() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public Set<GetBookTagResponseSchema> getTags() {
        return Collections.emptySet();
    }


    @Schema(
            description = "The value will always set to 'EBOOK'",
            implementation = String.class,
            allowableValues = {"EBOOK"})
    @NotEmpty
    public BookTypeSchema getType() {
        return null;
    }

    @Schema(description = "format of ebook")
    @Override
    public EBookFormat getFormat() {
        return null;
    }

}
