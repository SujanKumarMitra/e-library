package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Set;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@Schema(description = "Payload to update a package." +
        "<br> if a field is missing or set to null, it's value will remain unchanged")
public class UpdatePackageRequestSchema extends Package {
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
    @SuppressWarnings("unchecked")
    @Schema(description = "if it is null, then no changes will be made." +
            "<br>. if it is empty or filled array, then previous items will be replaced by this new items." +
            "<br>. For individual updates see `PATCH /api/v1/packages/{bookId}/items/{itemId}`")
    public Set<UpdatePackageItemRequestSchema> getItems() {
        return Collections.emptySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Schema(description = "if it is null, then no changes will be made." +
            "<br>. if it is empty or filled array, then previous tags will be replaced by this new tags." +
            "<br>. For individual updates see `PATCH /api/v1/packages/{bookId}/tags/{tagId}`")
    public Set<UpdatePackageTagRequestSchema> getTags() {
        return Collections.emptySet();
    }
}
