package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface EBookProcessor {

    void process(EbookProcessDetails processDetails);

    boolean supports(EBookFormat format);
}
