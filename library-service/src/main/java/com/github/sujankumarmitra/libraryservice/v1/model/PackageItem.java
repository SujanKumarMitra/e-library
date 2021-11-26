package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public abstract class PackageItem {

    public abstract String getId();

    public abstract String getPackageId();

    public abstract String getBookId();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PackageItem)) return false;
        PackageItem item = (PackageItem) o;
        return Objects.equals(getId(), item.getId()) &&
                Objects.equals(getPackageId(), item.getPackageId()) &&
                Objects.equals(getBookId(), item.getBookId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getPackageId(),
                getBookId());
    }

    @Override
    public String toString() {
        return "PackageItem{" +
                "id='" + getId() + '\'' +
                ", packageId='" + getPackageId() + '\'' +
                ", bookId='" + getBookId() + '\'' +
                '}';
    }
}
