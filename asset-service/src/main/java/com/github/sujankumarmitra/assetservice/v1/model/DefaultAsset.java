package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.Data;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Data
public class DefaultAsset implements Asset {
    private final String id;
    private final String name;
}
