package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@Schema(description = "Payload when lease request is being accepted")
public class AcceptLeaseRequestRequestSchema extends AcceptedLease {

    @Schema(hidden = true)
    public String getLeaseRequestId() {
        return null;
    }

    @Schema(implementation = String.class,
            allowableValues = {"ACCEPTED"},
            description = "the value must be set to 'ACCEPTED'"
    )
    @NotEmpty
    public LeaseStatus getStatus() {
        return null;
    }

    @Schema(description = "the timestamp from which the lease will take effect. Represented in UNIX epoch milliseconds." +
            "<br> start time must be present or future timestamp")
    @NotEmpty
    public Long getStartTimeInEpochMilliseconds() {
        return 0L;
    }

    @Schema(description = "the duration of the lease which will take effect after <i>startTime</i>." +
            "<br> The value must be either <i>-1</i>, which depicts infinite duration (generally given to teachers), or a positive value." +
            "<br> The time unit is in milliseconds")
    @NotEmpty
    public Long getDurationInMilliseconds() {
        return 0L;
    }

}
