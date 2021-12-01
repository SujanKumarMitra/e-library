package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create a package item")
public class CreatePackageItemRequestSchema extends PackageItem {
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
    @Schema(description = "the id of the book")
    @NotEmpty
    public String getBookId() {
        return null;
    }
}
