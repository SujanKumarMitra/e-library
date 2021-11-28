package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
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
public class R2dbcPostgresqlAuthorDao implements AuthorDao {

    public static final String INSERT_STATEMENT = "INSERT INTO authors(book_id,name) VALUES ($1,$2) ON CONFLICT ON CONSTRAINT unq_authors_book_id_name DO NOTHING RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id,book_id,name FROM authors WHERE book_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE authors SET name=$1 WHERE id=$2";
    public static final String DELETE_STATEMENT = "DELETE FROM authors WHERE book_id=$1";

    @NonNull
    private final DatabaseClient databaseClient;

    @Override
    @Transactional
    public Flux<String> createAuthors(Collection<? extends Author> authors) {
        return Flux.defer(() -> {
            if (authors == null) {
                log.debug("given authors is null");
                return Flux.error(new NullPointerException("given argument authors is null"));
            }
            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(INSERT_STATEMENT);

                        for (Author author : authors) {
                            String bookId = author.getBookId();
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(bookId);
                            } catch (IllegalArgumentException ex) {
                                log.debug("{} is not a valid uuid, returning Flux.error(BookNotFoundException)", bookId);
                                return Flux.error(new BookNotFoundException(bookId));
                            }
                            statement = statement
                                    .bind("$1", uuid)
                                    .bind("$2", author.getName())
                                    .add();
                        }

                        return Flux.from(statement.execute());
                    })
                    .flatMapSequential(result -> result.map((row, rowMetadata) -> row.get("id", UUID.class)))
                    .onErrorMap(R2dbcDataIntegrityViolationException.class, err -> {
                        log.debug("DB integrity error {}", err.getMessage());
                        return new BookNotFoundException(
                                List.of(new DefaultErrorDetails("some bookId(s) is/are invalid")));
                    })
                    .map(Object::toString);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Author> getAuthorsByBookId(String bookId) {
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
                    .map(this::mapToR2dbcAuthor)
                    .all()
                    .cast(Author.class);
        });

    }

    @Override
    @Transactional
    public Mono<Void> updateAuthors(Collection<? extends Author> authors) {
        return Mono.defer(() -> {
            if (authors == null) {
                log.debug("given authors is null");
                return Mono.error(new NullPointerException("given authors is null"));
            }
            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(UPDATE_STATEMENT);
                        for (Author author : authors) {
                            String id = author.getId();
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(id);
                            } catch (IllegalArgumentException e) {
                                log.debug("{} is not valid uuid, skipping update", id);
                                continue;
                            }
                            statement = statement
                                    .bind("$1", author.getName())
                                    .bind("$2", uuid)
                                    .add();
                        }

                        return Flux.from(statement.execute());
                    }).flatMap(Result::getRowsUpdated)
                    .reduce(Integer::sum)
                    .doOnSuccess(updateCount -> log.debug("author update count {}", updateCount))
                    .then();
        });
    }

    @Override
    @Transactional
    public Mono<Void> deleteAuthorsByBookId(String bookId) {
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
                    .sql(DELETE_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(deleteCount -> log.debug("authors delete count: {}", deleteCount))
                    .then();
        });
    }

    private R2dbcAuthor mapToR2dbcAuthor(Row row) {
        R2dbcAuthor author = new R2dbcAuthor();

        author.setId(row.get("id", UUID.class));
        author.setBookId(row.get("book_id", UUID.class));
        author.setName(row.get("name", String.class));

        return author;
    }
}
