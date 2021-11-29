package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(name = "CreatePackageRequest")
public class OpenApiCreatePackageRequest extends Package {
    @Override
    @Schema
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<OpenApiCreatePackageItem> getItems() {
        return null;
    }

    @Override
    public Set<OpenApiCreatePackageTag> getTags() {
        return null;
    }
}
