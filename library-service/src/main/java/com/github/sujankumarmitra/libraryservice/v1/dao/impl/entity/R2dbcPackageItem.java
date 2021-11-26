package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@Getter
@Setter
public class R2dbcPackageItem extends PackageItem {
    private UUID id;
    private UUID packageId;
    private UUID bookId;

    public R2dbcPackageItem() {
    }

    public R2dbcPackageItem(PackageItem item) {
        this.id = item.getId() == null ? null : UUID.fromString(item.getBookId());
        this.packageId = item.getPackageId() == null ? null : UUID.fromString(item.getPackageId());
        this.bookId = item.getBookId() == null ? null : UUID.fromString(item.getBookId());
    }

    @Override
    public String getId() {
        return nullableToString(id);
    }

    @Override
    public String getPackageId() {
        return nullableToString(packageId);
    }

    @Override
    public String getBookId() {
        return nullableToString(bookId);
    }

    private String nullableToString(Object id) {
        return id == null ? null : id.toString();
    }
}
