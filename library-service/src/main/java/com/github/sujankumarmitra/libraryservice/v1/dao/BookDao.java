package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface BookDao<B extends Book> {

    Mono<String> createBook(B book);

    Mono<B> getBook(String bookId);

    Mono<Void> updateBook(B book);

    Mono<Void> deleteBook(String bookId);

}
