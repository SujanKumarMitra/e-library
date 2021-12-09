package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.BookSearchDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlBookSearchDao implements BookSearchDao {

    public static final String SELECT_IDS_STATEMENT = "SELECT id FROM books LIMIT $2 OFFSET $1";
    public static final String SELECT_ID_BY_AUTHOR_AND_TITLE_STATEMENT = "SELECT * FROM (" +
            "(SELECT id FROM books WHERE title LIKE $1) " +
            " UNION " +
            "(SELECT book_id FROM authors WHERE name LIKE $2)) AS candidate_book_ids LIMIT $4 OFFSET $3";

    @NotNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public Flux<String> getBookIds(int skip, int limit) {
        return entityTemplate
                .getDatabaseClient()
                .sql(SELECT_IDS_STATEMENT)
                .bind("$1", skip)
                .bind("$2", limit)
                .map(row -> row.get("id", UUID.class))
                .all()
                .map(Object::toString);

    }

    @Override
    public Flux<String> getBookIdsByTitleAndAuthorStartingWith(String titlePrefix, String authorPrefix, int skip, int limit) {
        return entityTemplate
                .getDatabaseClient()
                .sql(SELECT_ID_BY_AUTHOR_AND_TITLE_STATEMENT)
                .bind("$1", titlePrefix + "%")
                .bind("$2", authorPrefix + "%")
                .bind("$3", skip)
                .bind("$4", limit)
                .map(row -> row.get(0, UUID.class))
                .all()
                .map(Object::toString);

    }
}