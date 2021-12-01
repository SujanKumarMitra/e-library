package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetPackageTagResponseSchema extends PackageTag {
    @Override
    @NotEmpty
    public String getPackageId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getKey() {
        return null;
    }

    @Override
    @NotEmpty
    public String getValue() {
        return null;
    }
}
