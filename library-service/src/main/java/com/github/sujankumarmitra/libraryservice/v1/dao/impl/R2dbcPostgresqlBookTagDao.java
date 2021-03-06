package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateTagKeyException;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlBookTagDao implements BookTagDao {

    public static final String INSERT_STATEMENT = "INSERT INTO book_tags(book_id,key,value) VALUES ($1,$2,$3) RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id, book_id, key, value FROM book_tags WHERE book_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE book_tags SET key=$1, value=$2 WHERE id=$3";
    public static final String DELETE_BY_BOOK_ID_STATEMENT = "DELETE FROM book_tags WHERE book_id=$1";
    public static final String DELETE_BY_ID_STATEMENT = "DELETE FROM book_tags WHERE id=$1";
    public static final String UNIQUE_TAG_KEY_CONSTRAINT_NAME = "unq_book_tags_book_id_key";

    @NonNull
    private final DatabaseClient databaseClient;

    private Throwable translateException(R2dbcDataIntegrityViolationException err) {
        log.debug("DB integrity error {}", err.getMessage());
        String message = err.getMessage();

        if (message.contains(UNIQUE_TAG_KEY_CONSTRAINT_NAME))
            return new DuplicateTagKeyException("tag with given key already exists for given bookId");
        else
            return new BookNotFoundException(
                    List.of(new DefaultErrorDetails("some bookId(s) is/are invalid")));
    }

    @Override
    @Transactional
    public Flux<String> createTags(Collection<? extends BookTag> tags) {
        return Flux.defer(() -> {
            if (tags == null) {
                log.debug("given tags is null");
                return Flux.error(new NullPointerException("given tags is null"));
            }

            if (tags.isEmpty()) {
                log.debug("empty tags. returning Empty Flux");
                return Flux.empty();
            }

            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(INSERT_STATEMENT);

                        for (BookTag tag : tags) {
                            String bookId = tag.getBookId();
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(bookId);
                            } catch (IllegalArgumentException ex) {
                                log.debug("{} is not a valid uuid, returning Flux.error(BookNotFoundException)", bookId);
                                return Flux.error(new BookNotFoundException(bookId));
                            }
                            statement = statement
                                    .bind("$1", uuid)
                                    .bind("$2", tag.getKey())
                                    .bind("$3", tag.getValue())
                                    .add();
                        }

                        return Flux.from(statement.execute());
                    })
                    .flatMapSequential(result -> result.map((row, rowMetadata) -> row.get("id", UUID.class)))
                    .onErrorMap(R2dbcDataIntegrityViolationException.class, this::translateException)
                    .map(Object::toString);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookTag> getTagsByBookId(String bookId) {
        return Flux.defer(() -> {
            if (bookId == null) {
                log.debug("given bookId is null");
                return Flux.error(new NullPointerException("given bookId is null"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(bookId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not valid uuid, return Flux.empty()", bookId);
                return Flux.empty();
            }
            return databaseClient
                    .sql(SELECT_STATEMENT)
                    .bind("$1", uuid)
                    .map(this::mapToR2dbcBookTag)
                    .all()
                    .cast(BookTag.class);
        });

    }

    @Override
    @Transactional
    public Mono<Void> deleteTagsByBookId(String bookId) {
        return Mono.defer(() -> {
            if (bookId == null) {
                log.debug("null bookId, return Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("bookId must be non-null"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(bookId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is invalid uuid, returning Mono.empty()", bookId);
                return Mono.empty();
            }

            return this.databaseClient
                    .sql(DELETE_BY_BOOK_ID_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(deleteCount -> log.debug("tags delete count: {}", deleteCount))
                    .then();
        });
    }

    private R2dbcBookTag mapToR2dbcBookTag(Row row) {
        R2dbcBookTag tag = new R2dbcBookTag();

        tag.setId(row.get("id", UUID.class));
        tag.setBookId(row.get("book_id", UUID.class));
        tag.setKey(row.get("key", String.class));
        tag.setValue(row.get("value", String.class));

        return tag;
    }
}
