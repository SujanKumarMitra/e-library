package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultAuthorService implements AuthorService {

    private final AuthorDao authorDao;

    @Override
    public Mono<String> createAuthor(Author author) {
        return authorDao.createAuthor(author);
    }

    @Override
    public Mono<Void> updateAuthor(Author author) {
        return authorDao.updateAuthor(author);
    }

    @Override
    public Mono<Void> deleteAuthor(String authorId) {
        return authorDao.deleteById(authorId);
    }
}
