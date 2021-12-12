package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.*;

import java.util.Objects;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Getter
@Setter
@Builder(builderMethodName = "newBuilder")
@AllArgsConstructor
public class DefaultAssetPermission extends AssetPermission {
    @NonNull
    private String assetId;
    @NonNull
    private String subjectId;
    @NonNull
    private Long grantStartEpochMilliseconds;
    @NonNull
    private Long grantDurationInMilliseconds;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
