package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface AuthorDao {

    Mono<Void> insertAuthors(Set<? extends Author> authors);

    Mono<Void> updateAuthors(Set<? extends Author> authors);
}
