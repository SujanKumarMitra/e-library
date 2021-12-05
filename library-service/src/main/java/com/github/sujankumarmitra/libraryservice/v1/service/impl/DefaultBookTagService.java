package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import com.github.sujankumarmitra.libraryservice.v1.service.BookTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultBookTagService implements BookTagService {

    private final BookTagDao bookTagDao;


    @Override
    public Mono<String> createTag(BookTag tag) {
        return bookTagDao.createTag(tag);
    }

    @Override
    public Mono<Void> updateTag(BookTag tag) {
        return bookTagDao.updateTag(tag);
    }

    @Override
    public Mono<Void> deleteTag(String tagId) {
        return bookTagDao.deleteTagById(tagId);
    }
}
