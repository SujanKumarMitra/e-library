package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@NoArgsConstructor
public class GrantPermissionRequest implements AssetPermission {
    @JsonIgnore
    private String assetId;
    @Schema(
            description = "ID of the client of whom permission will be granted"
    )
    private String subjectId;
    @Schema(
            description = "timestamp from when the grant will take effect"
    )
    private long grantStartEpochMilliseconds;
    @Schema(
            description = "duration after the grant start time, post that," +
                    " the grant will expire and client can no longer access the asset"
    )
    private long grantDurationInMilliseconds;
}
