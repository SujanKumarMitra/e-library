package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssetRequest implements Asset {
    @Schema(description = "a name which will be associated with binary object")
    private String name;

    @JsonIgnore
    public String getId() {
        return null;
    }
}
