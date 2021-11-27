package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
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

    public R2dbcPackage(@NonNull Package _package) {
        this.id = _package.getId() == null ? null : UUID.fromString(_package.getId());
        this.name = _package.getName();
        if (_package.getItems() != null) {
            for (PackageItem packageItem : _package.getItems()) {
                this.items.add(new R2dbcPackageItem(packageItem));
            }
        }
        if (_package.getTags() != null) {
            for (PackageTag tag : _package.getTags()) {
                this.tags.add(new R2dbcPackageTag(tag));
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

}
