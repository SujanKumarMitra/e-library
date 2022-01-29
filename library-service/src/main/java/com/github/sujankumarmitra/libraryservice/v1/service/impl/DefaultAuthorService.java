package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.service.AuthorService;
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
    public Mono<String> createAuthor(BookAuthor bookAuthor) {
        return authorDao.createAuthor(bookAuthor);
    }

    @Override
    public Mono<Void> updateAuthor(BookAuthor bookAuthor) {
        return authorDao.updateAuthor(bookAuthor);
    }

    @Override
    public Mono<Void> deleteAuthor(String authorId) {
        return authorDao.deleteById(authorId);
    }
}
