package com.github.sujankumarmitra.libraryservice.v1.dao;

import reactor.core.publisher.Flux;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
public interface BookSearchDao {

    Flux<String> getBookIds(int skip, int limit);

    Flux<String> getBookIdsByTitleAndAuthorStartingWith(String titlePrefix, String authorPrefix, int skip, int limit);

}
