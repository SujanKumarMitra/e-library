package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Package {

    public abstract String getId();

    public abstract String getName();

    public abstract Set<? extends PackageItem> getItems();

    public abstract Set<? extends PackageTag> getTags();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Package)) return false;

        Package that = (Package) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getItems(), that.getItems()) &&
                Objects.equals(getTags(), that.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getName(),
                getItems(),
                getTags());
    }

    @Override
    public String toString() {
        return "Package{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", items=" + getItems() +
                ", tags=" + getTags() +
                '}';
    }

}
