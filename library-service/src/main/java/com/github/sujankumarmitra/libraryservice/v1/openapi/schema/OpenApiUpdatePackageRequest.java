package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "UpdatePackageRequest")
public class OpenApiUpdatePackageRequest extends Package {
    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<OpenApiUpdatePackageItemRequest> getItems() {
        return null;
    }

    @Override
    public Set<OpenApiUpdatePackageTagRequest> getTags() {
        return null;
    }
}
