package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface BookDao {

    Mono<String> createBook(Book book);

    Mono<Book> getBook(String bookId);

    Mono<Void> updateBook(Book book);

    Mono<Void> deleteBook(String bookId);

}
