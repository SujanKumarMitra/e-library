package com.github.sujankumarmitra.assetservice.v1.dto;

import lombok.Data;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Data
public class DefaultGetAssetRequest implements GetAssetRequest {
    private final String assetId;
}
