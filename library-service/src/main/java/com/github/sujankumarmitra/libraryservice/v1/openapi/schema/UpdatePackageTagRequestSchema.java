package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to update a package tag.")
public class UpdatePackageTagRequestSchema extends PackageTag {
    @Override
    @Schema(hidden = true)
    public String getPackageId() {
        return null;
    }

    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    @Schema(description = "the new key of the tag. the key must be unique")
    @NotEmpty
    public String getKey() {
        return null;
    }

    @Override
    @Schema(description = "the new value of the tag")
    @NotEmpty
    public String getValue() {
        return null;
    }
}
