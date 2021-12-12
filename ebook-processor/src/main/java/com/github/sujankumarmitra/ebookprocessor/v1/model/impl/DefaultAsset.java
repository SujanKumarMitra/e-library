package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.AccessLevel;
import com.github.sujankumarmitra.ebookprocessor.v1.model.Asset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 12/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultAsset extends Asset {
    private String name;
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
