package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface AuthorDao {

    default Mono<String> createAuthor(Author author) {
        return createAuthors(List.of(author)).next();
    }

    Flux<String> createAuthors(Collection<? extends Author> authors);

    Flux<Author> getAuthorsByBookId(String bookId);

    Mono<Void> updateAuthors(Collection<? extends Author> authors);

    default Mono<Void> updateAuthor(Author author) {
        return updateAuthors(List.of(author));
    }

    Mono<Void> deleteAuthorsByBookId(String bookId);

    Mono<Void> deleteById(String authorId);
}
