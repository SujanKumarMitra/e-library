package com.github.sujankumarmitra.assetservice.v1.model;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
public abstract class AbstractAsset implements Asset {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;

        Asset asset = (Asset) o;

        if (!getId().equals(asset.getId())) return false;
        return getName().equals(asset.getName());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
