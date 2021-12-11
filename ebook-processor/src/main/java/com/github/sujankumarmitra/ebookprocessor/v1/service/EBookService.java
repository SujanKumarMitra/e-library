package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface EBookService {

    Mono<EBook> getEBook(String ebookId);

}
