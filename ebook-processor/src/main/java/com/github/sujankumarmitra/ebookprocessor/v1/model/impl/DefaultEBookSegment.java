package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
public class DefaultEBookSegment extends EBookSegment {
    private String bookId;
    private Integer index;
    private String assetId;
}
