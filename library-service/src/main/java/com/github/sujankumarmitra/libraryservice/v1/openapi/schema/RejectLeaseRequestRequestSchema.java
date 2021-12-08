package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@Schema(description = "Payload when lease request is being rejected")
public class RejectLeaseRequestRequestSchema extends RejectedLease {

    @Schema(hidden = true)
    public String getLeaseRequestId() {
        return null;
    }

    @Schema(implementation = String.class,
            allowableValues = {"REJECTED"},
            description = "the value must be set to 'REJECTED'"
    )
    @NotEmpty
    public LeaseStatus getStatus() {
        return null;
    }

    @Schema(description = "a human readable reason for request rejection")
    @NotEmpty
    public String getReasonPhrase() {
        return null;
    }

}
