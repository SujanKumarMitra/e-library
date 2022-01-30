package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetPendingLeaseRequestResponseSchema extends LeaseRequest {
    @Override
    @NotEmpty
    @Schema(description = "id of lease request")
    public String getId() {
        return null;
    }

    @Override
    public String getLibraryId() {
        return null;
    }

    @Override
    @NotEmpty
    @Schema(description = "id of the book to be leased")
    public String getBookId() {
        return null;
    }

    @Override
    @NotEmpty
    @Schema(description = "id of the user who made the request")
    public String getUserId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public LeaseStatus getStatus() {
        return null;
    }

    @Override
    @NotNull
    @Schema(description = "the timestamp when the lease request is being made. Represented in UNIX epoch milliseconds")
    public Long getTimestamp() {
        return 0L;
    }
}
