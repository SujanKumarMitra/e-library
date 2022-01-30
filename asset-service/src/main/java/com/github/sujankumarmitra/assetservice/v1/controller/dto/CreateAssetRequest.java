package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.AccessLevel;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateAssetRequestSchema", description = "Schema for creating a new Asset")
public class CreateAssetRequest extends Asset {

    @JsonIgnore
    private String id;

    @Schema(description = "a name which will be associated with binary object")
    @NotEmpty
    private String name;

    @Schema(description = "id of library of which this asset belongs to")
    @NotEmpty
    private String libraryId;

    @Schema(description = "type of asset object, see HTTP Content-Type header")
    private String mimeType;

    @Schema(description = "access level of the asset")
    @NotNull
    private AccessLevel accessLevel;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
