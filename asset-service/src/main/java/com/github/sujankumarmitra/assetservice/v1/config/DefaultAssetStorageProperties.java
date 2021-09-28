package com.github.sujankumarmitra.assetservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@ConfigurationProperties("app.storage")
public class DefaultAssetStorageProperties implements AssetStorageProperties {
    private String baseDir;
}
