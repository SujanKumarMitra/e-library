package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Schema for creating a new Asset")
public class CreateAssetRequest implements Asset {
    @Schema(
            title = "a name which will be associated with binary object",
            minLength = 1
    )
    @NotNull(message = "cannot be null")
    @NotBlank(message = "cannot be blank")
    private String name;

    @JsonIgnore
    public String getId() {
        return null;
    }
}
