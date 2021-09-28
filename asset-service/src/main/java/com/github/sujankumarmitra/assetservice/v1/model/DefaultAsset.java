package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.*;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class DefaultAsset extends Asset {
    @NonNull
    private final String id;
    @NonNull
    private final String name;
}
