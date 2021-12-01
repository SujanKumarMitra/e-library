package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetLeaseRequestResponseSchema extends LeaseRequest {
    @Override
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getBookId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getUserId() {
        return null;
    }

    @Override
    @NotEmpty
    public LeaseStatus getStatus() {
        return null;
    }

    @Override
    @Schema(description = "the timestamp when the request is being made. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public long getTimestamp() {
        return 0;
    }
}
