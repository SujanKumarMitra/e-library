package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
public class CreatePackageRequestSchema extends Package {
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
    public Set<CreatePackageItemSchema> getItems() {
        return null;
    }

    @Override
    public Set<CreatePackageTagSchema> getTags() {
        return null;
    }
}
