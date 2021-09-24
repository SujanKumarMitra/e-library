package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dto.*;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetStorageService {

    StoreAssetResponse storeAsset(StoreAssetRequest request);

    RetrieveAssetResponse retrieveAsset(RetrieveAssetRequest request);

    PurgeAssetResponse purgeAsset(PurgeAssetRequest request);
}
