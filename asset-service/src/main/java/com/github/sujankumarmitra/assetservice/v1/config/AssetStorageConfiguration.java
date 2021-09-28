package com.github.sujankumarmitra.assetservice.v1.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Configuration
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(DefaultAssetStorageProperties.class)
public class AssetStorageConfiguration implements InitializingBean {

    private final AssetStorageProperties assetStorageProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Using {}", assetStorageProperties);
    }
}