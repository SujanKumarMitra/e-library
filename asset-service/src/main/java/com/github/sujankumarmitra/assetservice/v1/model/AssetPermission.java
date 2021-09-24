package com.github.sujankumarmitra.assetservice.v1.model;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetPermission {

    String getAssetId();

    String getSubjectId();

    long getGrantStartTime();

    long getGrantDuration();
}
