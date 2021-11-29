package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class CreatePhysicalBookRequestSchema extends PhysicalBook {
    @Schema(hidden = true)
    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Set<CreateAuthorSchema> getAuthors() {
        return null;
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
    public String getCoverPageImageId() {
        return null;
    }

    @Override
    public Set<CreateBookTagSchema> getTags() {
        return null;
    }

    @Override
    public long getCopiesAvailable() {
        return 0;
    }

    @Override
    public MoneySchema getFinePerDay() {
        return null;
    }
}
