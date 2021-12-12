package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.AccessLevel;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Schema for creating a new Asset")
public class CreateAssetRequest extends Asset {

    @JsonIgnore
    private String id;

    @Schema(description = "a name which will be associated with binary object")
    @NotEmpty
    private String name;

    @JsonIgnore
    private String ownerId;

    @Schema(description = "access level of the asset")
    @NotNull
    private AccessLevel accessLevel;

}
