package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static java.lang.System.currentTimeMillis;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@NoArgsConstructor
@Schema(name = "GrantPermissionRequest", description = "Payload for granting access to an asset")
public class GrantPermissionRequest extends AssetPermission {
    @JsonIgnore
    private String assetId;

    @Schema(
            title = "ID of the client of whom permission will be granted",
            minLength = 1
    )
    @NotNull
    @NotBlank
    private String subjectId;

    @Schema(
            title = "timestamp from when the grant will take effect",
            description = "must be present or future"

    )
    @NotNull
    private Long grantStartEpochMilliseconds;

    @Schema(
            title = "duration after the grant start time, post that," +
                    " the grant will expire and client can no longer access the asset",
            description = "must be either -1 or positive"
    )
    @NotNull
    private Long grantDurationInMilliseconds;

    @AssertTrue(message = "must be present or future")
    @JsonIgnore
    public boolean isValidGrantStartEpochMilliseconds() {
        return grantStartEpochMilliseconds >= currentTimeMillis();
    }

    @AssertTrue(message = "must be either -1 or positive")
    @JsonIgnore
    public boolean isValidGrantDurationInMilliseconds() {
        return grantDurationInMilliseconds == -1 ||
                grantDurationInMilliseconds > 0;
    }


}
