package com.github.sujankumarmitra.assetservice.v1.model;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class Asset {

    public abstract String getId();

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;

        Asset that = (Asset) o;

        if (!getId().equals(that.getId())) return false;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

}
