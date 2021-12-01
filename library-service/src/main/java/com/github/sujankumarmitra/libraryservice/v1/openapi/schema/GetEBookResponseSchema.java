package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.Ebook;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetEBookResponseSchema extends Ebook {
    @Override
    @NotEmpty
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
    @NotEmpty
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
    public String getCoverPageImageId() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public Set<GetBookTagResponseSchema> getTags() {
        return Collections.emptySet();
    }


    @Schema(
            description = "The value will always set to 'ebook'",
            implementation = String.class,
            allowableValues = {"ebook"})
    @NotEmpty
    public BookTypeSchema getType() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public <T extends EBookSegment> List<T> getSegments() {
        return Collections.emptyList();
    }
}
