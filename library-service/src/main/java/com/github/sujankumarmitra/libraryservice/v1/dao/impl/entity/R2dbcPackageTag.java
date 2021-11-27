package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 27/11/21, 2021
 */
@Getter
@Setter
public final class R2dbcPackageTag extends PackageTag {

    private UUID id;
    private UUID packageId;
    private String key;
    private String value;

    public R2dbcPackageTag() {
    }

    public R2dbcPackageTag(@NonNull PackageTag tag) {
        this.id = tag.getId() == null ? null : UUID.fromString(tag.getId());
        this.packageId = tag.getPackageId() == null ? null : UUID.fromString(tag.getPackageId());
        this.key = tag.getKey();
        this.value = tag.getValue();
    }

    public UUID getUuidId() {
        return id;
    }

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    public String getPackageId() {
        return packageId == null ? null : packageId.toString();
    }

    public UUID getPackageUuid() {
        return packageId;
    }
}
