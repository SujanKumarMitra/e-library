package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class UpdatePackageItemSchema extends PackageItem {
    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public String getPackageId() {
        return null;
    }

    @Override
    public String getBookId() {
        return null;
    }
}
