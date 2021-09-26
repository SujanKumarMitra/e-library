package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@Builder(builderMethodName = "newBuilder")
@AllArgsConstructor
public class DefaultAssetPermission implements AssetPermission {
    @NonNull
    private String assetId;
    @NonNull
    private String subjectId;
    @NonNull
    private long grantStartEpochMilliseconds;
    @NonNull
    private long grantDurationInMilliseconds;
}
