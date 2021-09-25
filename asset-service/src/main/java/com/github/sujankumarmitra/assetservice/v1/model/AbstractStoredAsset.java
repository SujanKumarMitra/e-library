package com.github.sujankumarmitra.assetservice.v1.model;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
public abstract class AbstractStoredAsset implements StoredAsset {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoredAsset)) return false;

        StoredAsset asset1 = (StoredAsset) o;

        return getAsset().equals(asset1.getAsset());
    }

    @Override
    public int hashCode() {
        return getAsset().hashCode();
    }
}
