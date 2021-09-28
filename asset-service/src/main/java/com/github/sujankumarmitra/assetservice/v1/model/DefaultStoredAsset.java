package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.*;
import org.springframework.core.io.InputStreamSource;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class DefaultStoredAsset extends StoredAsset {
    @NonNull
    private final Asset asset;
    @NonNull
    private final InputStreamSource inputStreamSource;
}
