package com.github.sujankumarmitra.assetservice.v1.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;


/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Data
@Validated
@ConfigurationProperties("app.storage")
public class DefaultAssetStorageProperties extends AssetStorageProperties {
    @NotEmpty
    private String baseDir;
}
