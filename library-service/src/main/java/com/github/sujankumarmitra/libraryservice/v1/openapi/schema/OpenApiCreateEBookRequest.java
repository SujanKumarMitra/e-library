package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.Ebook;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "CreateEBookRequest")
public class OpenApiCreateEBookRequest extends Ebook {
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
    public Set<OpenApiCreateAuthorRequest> getAuthors() {
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
    public Set<OpenApiCreateBookTagRequest> getTags() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public List<EBookSegment> getSegments() {
        return null;
    }
}
