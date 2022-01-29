package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

import static java.lang.Boolean.FALSE;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
public class GetActiveAcceptedLeaseRequestResponseSchema extends AcceptedLease {
    @Override
    @Schema(description = "the id of lease request")
    @NotEmpty
    public String getLeaseRequestId() {
        return null;
    }

    @Override
    @Schema(description = "the timestamp at which lease will take effect. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public Long getStartTimeInEpochMilliseconds() {
        return null;
    }

    @Override
    @Schema(description = "the duration for which the lease will be active after <i>startTime</i>." +
            "<br> The value must be either <i>-1</i>, which depicts infinite duration (generally given to teachers), or a positive value." +
            "<br> The time unit is in milliseconds")
    @NotEmpty
    public Long getDurationInMilliseconds() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public Boolean isRelinquished() {
        return FALSE;
    }
}
