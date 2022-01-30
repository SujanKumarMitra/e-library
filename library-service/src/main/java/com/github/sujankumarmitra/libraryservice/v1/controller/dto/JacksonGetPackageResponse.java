package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Set;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@AllArgsConstructor
public class JacksonGetPackageResponse extends Package {

    @NonNull
    @JsonIgnore
    private final Package aPackage;

    @Override
    public String getId() {
        return aPackage.getId();
    }

    @Override
    public String getLibraryId() {
        return aPackage.getLibraryId();
    }

    @Override
    public String getName() {
        return aPackage.getName();
    }

    @Override
    @JsonSerialize(contentAs = PackageItem.class)
    public Set<? extends PackageItem> getItems() {
        return aPackage.getItems();
    }

    @Override
    @JsonSerialize(contentAs = PackageTag.class)
    public Set<? extends PackageTag> getTags() {
        return aPackage.getTags();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
