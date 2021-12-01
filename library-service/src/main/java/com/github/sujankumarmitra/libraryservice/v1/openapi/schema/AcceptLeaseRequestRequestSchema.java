package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@Schema(description = "Payload when lease request is being accepted")
public class AcceptLeaseRequestRequestSchema {

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

    @Schema(description = "the timestamp from which the lease will take effect. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public long getStartTime() {
        return 0L;
    }

    @Schema(description = "the timestamp from which the lease will expire. Represented in UNIX epoch milliseconds")
    @NotEmpty
    public long getEndTime() {
        return 0L;
    }

}
