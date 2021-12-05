package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
public interface BookService {

    Mono<String> createBook(PhysicalBook book);

    Mono<String> createBook(EBook book);

    Mono<Book> getBook(String bookId);

    Mono<Void> updateBook(PhysicalBook book);

    Mono<Void> updateBook(EBook book);

    Mono<Void> deleteBook(String bookId);

}
