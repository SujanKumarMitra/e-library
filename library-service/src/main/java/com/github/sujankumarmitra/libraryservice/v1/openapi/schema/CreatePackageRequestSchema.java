package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @Schema(name = "name of the package")
    @NotEmpty
    public String getName() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotEmpty
    public Set<CreatePackageItemSchema> getItems() {
        return Collections.emptySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public Set<CreatePackageTagRequestSchema> getTags() {
        return Collections.emptySet();
    }
}
