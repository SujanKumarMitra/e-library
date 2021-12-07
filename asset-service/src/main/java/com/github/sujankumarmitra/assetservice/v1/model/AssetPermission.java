package com.github.sujankumarmitra.assetservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class AssetPermission {

    public static final long INFINITE_GRANT_DURATION = -1;

    public abstract String getAssetId();

    public abstract String getSubjectId();

    public abstract long getGrantStartEpochMilliseconds();

    public abstract long getGrantDurationInMilliseconds();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetPermission)) return false;
        AssetPermission that = (AssetPermission) o;
        return getGrantStartEpochMilliseconds() == that.getGrantStartEpochMilliseconds() &&
         getGrantDurationInMilliseconds() == that.getGrantDurationInMilliseconds() && 
         getAssetId().equals(that.getAssetId()) && 
         getSubjectId().equals(that.getSubjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetId(), getSubjectId(), getGrantStartEpochMilliseconds(), getGrantDurationInMilliseconds());
    }
}