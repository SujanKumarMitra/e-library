package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@Setter
public class DefaultEBook extends EBook {
    String id;
    EBookFormat format;

}
