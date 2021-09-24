package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.dto.CreateAssetResponse;
import com.github.sujankumarmitra.assetservice.v1.dto.DeleteAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.dto.DeleteAssetResponse;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetService {

    CreateAssetResponse createAsset(CreateAssetRequest request);

    DeleteAssetResponse deleteAsset(DeleteAssetRequest request);
}
