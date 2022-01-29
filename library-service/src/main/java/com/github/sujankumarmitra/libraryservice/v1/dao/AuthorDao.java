package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface AuthorDao {

    default Mono<String> createAuthor(BookAuthor bookAuthor) {
        return createAuthors(List.of(bookAuthor)).next();
    }

    Flux<String> createAuthors(Collection<? extends BookAuthor> authors);

    Flux<BookAuthor> getAuthorsByBookId(String bookId);

    Mono<Void> updateAuthors(Collection<? extends BookAuthor> authors);

    default Mono<Void> updateAuthor(BookAuthor bookAuthor) {
        return updateAuthors(List.of(bookAuthor));
    }

    Mono<Void> deleteAuthorsByBookId(String bookId);

    Mono<Void> deleteById(String authorId);
}
