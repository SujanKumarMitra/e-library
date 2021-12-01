package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
public class GetActiveLeaseRequestResponseSchema extends LeaseRecord {
    @Override
    @Schema(description = "the id of lease request")
    @NotEmpty
    public String getLeaseRequestId() {
        return null;
    }

    @Override
    @Schema(description = "the timestamp at which lease will take effect. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public long getStartTime() {
        return 0;
    }

    @Override
    @Schema(description = "the timestamp at which lease will expire. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public long getEndTime() {
        return 0;
    }

    @Override
    @Schema(hidden = true)
    public boolean isRelinquished() {
        return false;
    }
}
