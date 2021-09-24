package com.github.sujankumarmitra.assetservice.v1.dto;

import java.io.InputStream;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface StoreAssetRequest {

    String getAssetId();

    InputStream getAssetInputStream();
}
