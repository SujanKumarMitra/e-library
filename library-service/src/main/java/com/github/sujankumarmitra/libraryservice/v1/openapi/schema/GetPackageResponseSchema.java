package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class GetPackageResponseSchema extends Package {
    @Override
    @NotEmpty
    public String getId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getLibraryId() {
        return null;
    }

    @Override
    @NotEmpty
    public String getName() {
        return null;
    }

    @Override
    @NotEmpty
    public Set<GetPackageItemResponseSchema> getItems() {
        return Collections.emptySet();
    }

    @Override
    @NotEmpty
    public Set<GetPackageTagResponseSchema> getTags() {
        return Collections.emptySet();
    }
}
