package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetRejectedLeaseRequestResponseSchema {

    @Schema(description = "the lease request id")
    @NotEmpty
    public String getLeaseRequestId() {
        return null;
    }

    @Schema(hidden = true)
    public LeaseStatus getStatus() {
        return null;
    }

    @Schema(description = "The reason due to which lease got rejected")
    @NotEmpty
    public String getReason() {
        return null;
    }

}