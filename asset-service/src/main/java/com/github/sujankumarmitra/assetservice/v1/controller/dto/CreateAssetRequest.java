package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import lombok.Data;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
public class CreateAssetRequest implements Asset {
    private String name;

    public String getId() {
        return null;
    }
}
