package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dto.CheckPermissionRequest;
import com.github.sujankumarmitra.assetservice.v1.dto.CheckPermissionResponse;
import com.github.sujankumarmitra.assetservice.v1.dto.GrantPermissionRequest;
import com.github.sujankumarmitra.assetservice.v1.dto.GrantPermissionResponse;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetPermissionService {

    GrantPermissionResponse grantPermission(GrantPermissionRequest request);

    CheckPermissionResponse checkPermission(CheckPermissionRequest request);
}
