package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Schema(description = "Payload for creating an ebook segment")
public class CreateEBookSegmentRequestSchema extends EBookSegment {
    @Schema(hidden = true)
    @Override
    public String getId() {
        return null;
    }

    @Schema(hidden = true)
    @Override
    public String getBookId() {
        return null;
    }

    @Override
    @NotNull
    @PositiveOrZero
    @Schema(description = "logical index of a segment, like array indices")
    public Integer getIndex() {
        return 0;
    }

    @Override
    @NotEmpty
    @Schema(description = "asset id of the segment")
    public String getAssetId() {
        return null;
    }
}
