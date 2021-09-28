package com.github.sujankumarmitra.assetservice.v1.model;

import org.springframework.core.io.InputStreamSource;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class StoredAsset {

    public abstract Asset getAsset();

    public abstract InputStreamSource getInputStreamSource();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultStoredAsset asset1 = (DefaultStoredAsset) o;

        return getAsset().equals(asset1.getAsset());
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + getAsset().hashCode();
        return result;
    }
}
