package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@Setter
public class DefaultEbookProcessDetails extends EbookProcessDetails {
    private String processId;
    private EBook book;
    private Path bookLocation;
    private AuthenticationToken authToken;

}
