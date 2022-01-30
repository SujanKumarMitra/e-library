package com.github.sujankumarmitra.ebookprocessor.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public abstract class Asset {

    public abstract String getName();

    public abstract String getLibraryId();

    public abstract String getMimeType();

    public abstract AccessLevel getAccessLevel();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;

        Asset asset = (Asset) o;
        return getName().equals(asset.getName()) &&
                Objects.equals(getLibraryId(), asset.getLibraryId()) &&
                Objects.equals(getMimeType(), asset.getMimeType()) &&
                getAccessLevel() == asset.getAccessLevel();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAccessLevel());
    }

    @Override
    public String toString() {
        return "Asset{" +
                "name='" + getName() + '\'' +
                ", accessLevel=" + getAccessLevel() +
                '}';
    }

}
