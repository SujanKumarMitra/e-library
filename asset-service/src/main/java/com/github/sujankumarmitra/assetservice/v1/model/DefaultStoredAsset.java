package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Data
public class DefaultStoredAsset implements StoredAsset {
    private final Asset asset;
    private final InputStreamSource inputStreamSource;
}
