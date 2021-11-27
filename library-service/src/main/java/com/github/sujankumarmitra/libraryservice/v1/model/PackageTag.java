package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class PackageTag extends Tag {

    public abstract String getPackageId();

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getPackageId(),
                getKey(),
                getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PackageTag)) return false;

        PackageTag other = (PackageTag) obj;
        return Objects.equals(getId(), other.getId()) &&
                Objects.equals(getPackageId(), other.getPackageId()) &&
                Objects.equals(getKey(), other.getKey()) &&
                Objects.equals(getValue(), other.getValue());
    }

    @Override
    public String toString() {
        return "PackageTag{" +
                "id='" + getId() + '\'' +
                ", packageId='" + getPackageId() + '\'' +
                ", key='" + getKey() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
