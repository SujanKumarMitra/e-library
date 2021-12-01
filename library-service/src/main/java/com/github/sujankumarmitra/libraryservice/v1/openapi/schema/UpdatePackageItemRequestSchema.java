package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class UpdatePackageItemRequestSchema extends PackageItem {
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
    @Schema(description = "the new book id")
    @NotEmpty
    public String getBookId() {
        return null;
    }
}
