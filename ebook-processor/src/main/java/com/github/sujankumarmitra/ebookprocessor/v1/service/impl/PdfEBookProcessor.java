package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat.PDF;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@Slf4j
public class PdfEBookProcessor implements EBookProcessor {
    @Override
    public void process(EbookProcessDetails processDetails) {
        log.info("{}", processDetails);
    }

    @Override
    public boolean supports(EBookFormat format) {
        return format == PDF;
    }
}
