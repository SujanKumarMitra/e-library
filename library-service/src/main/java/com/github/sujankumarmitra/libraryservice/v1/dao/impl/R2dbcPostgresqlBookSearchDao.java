package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.BookSearchDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    public static final String SELECT_IDS_STATEMENT = "SELECT id FROM books  WHERE library_id=$3 LIMIT $2 OFFSET $1";
    public static final String SELECT_ID_BY_AUTHOR_AND_TITLE_STATEMENT = "SELECT * FROM (" +
            "(SELECT id FROM books WHERE library_id=$5 AND title LIKE $1) " +
            " UNION " +
            "(SELECT authors.book_id FROM authors JOIN books ON (books.id=authors.book_id) WHERE library_id=$5 AND name LIKE $2)) AS candidate_book_ids LIMIT $4 OFFSET $3";

    @NotNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional(readOnly = true)
    public Flux<String> getBookIds(String libraryId, int skip, int limit) {
        return entityTemplate
                .getDatabaseClient()
                .sql(SELECT_IDS_STATEMENT)
                .bind("$1", skip)
                .bind("$2", limit)
                .bind("$3", libraryId)
                .map(row -> row.get("id", UUID.class))
                .all()
                .map(Object::toString);

    }

    @Override
    @Transactional(readOnly = true)
    public Flux<String> getBookIdsByTitleAndAuthorStartingWith(String libraryId, String titlePrefix, String authorPrefix, int skip, int limit) {
        DatabaseClient.GenericExecuteSpec executeSpec = entityTemplate
                .getDatabaseClient()
                .sql(SELECT_ID_BY_AUTHOR_AND_TITLE_STATEMENT)
                .bind("$3", skip)
                .bind("$4", limit)
                .bind("$5", libraryId);

        return bindNonNullParams(executeSpec, titlePrefix, authorPrefix)
                .map(row -> row.get(0, UUID.class))
                .all()
                .map(Object::toString);

    }

    private DatabaseClient.GenericExecuteSpec bindNonNullParams(DatabaseClient.GenericExecuteSpec executeSpec, String titlePrefix, String authorPrefix) {
        if (titlePrefix != null) {
            if (authorPrefix != null) {
                return executeSpec
                        .bind("$1", titlePrefix + "%")
                        .bind("$2", authorPrefix + "%");
            } else {
                return executeSpec
                        .bind("$1", titlePrefix + "%")
                        .bindNull("$2", String.class);
            }

        } else {
            if (authorPrefix != null) {
                return executeSpec
                        .bindNull("$1", String.class)
                        .bind("$2", authorPrefix + "%");
            } else {
                return executeSpec
                        .bind("$1", "%")
                        .bind("$2", "%");
            }
        }
    }
}