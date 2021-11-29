package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "UpdatePhysicalBookRequest")
public class OpenApiUpdatePhysicalBookRequest extends PhysicalBook {


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
    public Set<OpenApiUpdateAuthorRequest> getAuthors() {
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
    public Set<OpenApiUpdateBookTagRequest> getTags() {
        return null;
    }

    @Override
    public long getCopiesAvailable() {
        return 0;
    }

    @Override
    public Money getFinePerDay() {
        return null;
    }
}
