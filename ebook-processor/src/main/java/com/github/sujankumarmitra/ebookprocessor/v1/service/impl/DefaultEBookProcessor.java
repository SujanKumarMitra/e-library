package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessor;
import org.springframework.stereotype.Service;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
public class DefaultEBookProcessor implements EBookProcessor {
    @Override
    public void process(EbookProcessDetails processDetails) {
        System.out.println(processDetails);
        System.out.println("hello");
    }

    @Override
    public boolean supports(EBookFormat format) {
        return true;
    }
}
