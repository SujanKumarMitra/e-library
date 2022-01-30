package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookSegment;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface LibraryServiceClient {


    Mono<EBook> getEBook(String ebookId);

    Mono<String> saveEBookSegment(EBookSegment segment);

    Mono<Void> deleteEBookSegments(String bookId);
}
