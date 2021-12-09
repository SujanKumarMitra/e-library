package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
public interface PhysicalBookDao extends BookDao<PhysicalBook> {
    Mono<Void> decrementCopiesAvailable(String bookId);

    Mono<Void> incrementCopiesAvailable(String bookId);
}
