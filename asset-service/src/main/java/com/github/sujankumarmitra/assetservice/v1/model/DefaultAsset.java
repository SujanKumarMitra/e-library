package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.*;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultAsset extends Asset {
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String ownerId;
    @NonNull
    private AccessLevel accessLevel;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
