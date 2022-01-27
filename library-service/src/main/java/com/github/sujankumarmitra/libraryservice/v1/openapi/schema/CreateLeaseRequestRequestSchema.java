package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class CreateLeaseRequestRequestSchema extends LeaseRequest {
    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    @Schema
    @NotEmpty
    public String getLibraryId() {
        return null;
    }

    @Override
    @Schema(description = "the id of book to lease")
    @NotEmpty
    public String getBookId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public String getUserId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public LeaseStatus getStatus() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public Long getTimestamp() {
        return null;
    }
}