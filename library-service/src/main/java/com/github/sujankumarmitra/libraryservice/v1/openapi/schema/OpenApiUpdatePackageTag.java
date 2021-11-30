package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "UpdatePackageTag")
public class OpenApiUpdatePackageTag extends PackageTag {
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
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
