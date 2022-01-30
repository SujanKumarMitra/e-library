package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.github.sujankumarmitra.assetservice.v1.model.AccessLevel;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Jan 30/01/22, 2022
 */
@AllArgsConstructor
public class GetAssetResponse extends Asset {
    private final Asset delegate;

    @Schema(description = "id of the asset")
    @NotNull
    @Override
    public String getId() {
        return delegate.getId();
    }

    @Schema(description = "name of the asset")
    @NotNull
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Schema(description = "id of the owned library")
    @NotNull
    @Override
    public String getLibraryId() {
        return delegate.getLibraryId();
    }

    @Override
    @NotNull
    @Schema(description = "HTTP Response Content-Type header value to be set, when asset is fetched")
    public String getMimeType() {
        return delegate.getMimeType();
    }

    @Override
    @Schema(description = "privacy level of the asset")
    public AccessLevel getAccessLevel() {
        return delegate.getAccessLevel();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
