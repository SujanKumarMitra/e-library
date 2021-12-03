package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
@Schema(description = "Payload representing a segment of an ebook")
public class GetEBookSegmentResponse extends EBookSegment {
    @Override
    @Schema(description = "id of segment")
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    @Schema(description = "id of ebook")
    public String getBookId() {
        return null;
    }

    @Override
    @NotNull
    @Schema(description = "logical index of a segment, like array indices")
    public Long getIndex() {
        return 0L;
    }

    @Override
    @NotEmpty
    @Schema(description = "asset id of the segment")
    public String getAssetId() {
        return null;
    }
}
