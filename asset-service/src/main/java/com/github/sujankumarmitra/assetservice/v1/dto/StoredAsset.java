package com.github.sujankumarmitra.assetservice.v1.dto;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import org.springframework.core.io.InputStreamSource;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface StoredAsset {

    Asset getAsset();

    InputStreamSource getInputStreamSource();
}
