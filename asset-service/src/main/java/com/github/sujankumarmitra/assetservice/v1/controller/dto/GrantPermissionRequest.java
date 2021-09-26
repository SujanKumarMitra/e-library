package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@NoArgsConstructor
public class GrantPermissionRequest implements AssetPermission {
    private String assetId;
    private String subjectId;
    private long grantStartEpochMilliseconds;
    private long grantDurationInMilliseconds;
}
