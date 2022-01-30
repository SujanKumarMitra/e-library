package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to create a package")
public class CreatePackageRequestSchema extends Package {
    @Override
    @Schema(hidden = true)
    public String getId() {
        return null;
    }

    @Override
    @Schema
    @NotEmpty
    public String getLibraryId() {
        return null;
    }

    @Override
    @Schema(description = "name of the package")
    @NotEmpty
    public String getName() {
        return null;
    }

    @Override
    @NotNull
    @Size(min = 1)
    public Set<CreatePackageItemRequestSchema> getItems() {
        return Collections.emptySet();
    }

    @Override
    public Set<CreatePackageTagRequestSchema> getTags() {
        return Collections.emptySet();
    }
}
