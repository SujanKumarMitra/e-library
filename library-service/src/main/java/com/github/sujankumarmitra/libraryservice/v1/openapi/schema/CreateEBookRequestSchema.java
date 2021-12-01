package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.Ebook;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create an ebook")
public class CreateEBookRequestSchema extends Ebook {
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
    @Schema(description = "cover image asset id", nullable = true)
    public String getCoverPageImageId() {
        return null;
    }

    @Schema(
            implementation = String.class,
            allowableValues = {"ebook"},
            description = "the value must be set to 'ebook'"
    )
    @NotEmpty
    public BookTypeSchema getType() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public Set<CreateBookTagRequestSchema> getTags() {
        return Collections.emptySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Schema(hidden = true)
    public List<EBookSegment> getSegments() {
        return Collections.emptyList();
    }
}
