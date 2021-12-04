package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@Getter
@Setter
public final class R2dbcPackage extends Package {

    private UUID id;
    private String name;
    private Set<R2dbcPackageItem> items = new HashSet<>();
    private Set<R2dbcPackageTag> tags = new HashSet<>();

    public R2dbcPackage() {
    }

    public R2dbcPackage(@NonNull Package aPackage) {
        this.id = aPackage.getId() == null ? null : UUID.fromString(aPackage.getId());
        this.name = aPackage.getName();
        if (aPackage.getItems() != null) {
            for (PackageItem packageItem : aPackage.getItems()) {
                this.items.add(convertToR2dbcPackageItem(packageItem));
            }
        }
        if (aPackage.getTags() != null) {
            for (PackageTag tag : aPackage.getTags()) {
                this.tags.add(convertToR2dbcPackageTag(tag));
            }
        }
    }

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    public UUID getUuid() {
        return id;
    }


    public <T extends PackageTag> void addAllTags(Set<T> tags) {
        for (PackageTag tag : tags) {
            this.tags.add(convertToR2dbcPackageTag(tag));
        }
    }

    public <T extends PackageItem> void addAllItems(Set<T> items) {
        for (PackageItem item : items) {
            this.items.add(convertToR2dbcPackageItem(item));
        }
    }


    private R2dbcPackageItem convertToR2dbcPackageItem(PackageItem item) {
        try {
            return item instanceof R2dbcPackageItem ?
                    (R2dbcPackageItem) item :
                    new R2dbcPackageItem(item);
        } catch (IllegalArgumentException ex) {
            throw new BookNotFoundException(item.getBookId());
        }
    }

    private R2dbcPackageTag convertToR2dbcPackageTag(PackageTag tag) {
        return tag instanceof R2dbcPackageTag ?
                (R2dbcPackageTag) tag :
                new R2dbcPackageTag(tag);
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
