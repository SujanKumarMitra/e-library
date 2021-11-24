package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import io.r2dbc.postgresql.api.ErrorDetails;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlAuthorDao implements AuthorDao {

    public static final String UPSERT_STATEMENT = "INSERT into authors(book_id,name) VALUES($1,$2) ON CONFLICT ON CONSTRAINT pk_authors DO NOTHING";
    public static final String SELECT_STATEMENT = "SELECT book_id,name FROM authors WHERE book_id=$1";
    public static final BookNotFoundException DEFAULT_EXCEPTION = new BookNotFoundException(List.of(new DefaultErrorDetails("some book_id(s) is/are invalid")));
    @NonNull
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Void> insertAuthors(Set<? extends Author> authors) {
        return updateAuthors(authors);
    }

    @Override
    public Flux<Author> selectAuthors(String bookId) {
        return Flux.defer(() -> {
            if (bookId == null) {
                log.debug("given bookId is null");
                return Flux.error(new NullPointerException());
            }

            UUID id;
            try {
                id = UUID.fromString(bookId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not a valid uuid, returning empty flux", bookId);
                return Flux.empty();
            }

            return databaseClient
                    .sql(SELECT_STATEMENT)
                    .bind("$1", id)
                    .map(this::mapToR2dbcAuthor)
                    .all()
                    .cast(Author.class);
        });
    }

    private R2dbcAuthor mapToR2dbcAuthor(Row row) {
        R2dbcAuthor author = new R2dbcAuthor();

        author.setBookId(row.get("book_id", UUID.class));
        author.setName(row.get("name", String.class));

        return author;
    }

    @Override
    public Mono<Void> updateAuthors(Set<? extends Author> authors) {
        return Mono.defer(() -> {
            if (authors == null) {
                log.debug("given tags in param is null");
                return Mono.error(new NullPointerException("tags can't be null"));
            }
            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(UPSERT_STATEMENT);
                        for (Author author : authors) {
                            String id = author.getBookId();
                            String name = author.getName();

                            UUID uuid;
                            try {
                                uuid = UUID.fromString(id);
                            } catch (IllegalArgumentException ex) {
                                log.debug("{} in {} is an invalid uuid, returning Flux.error()", id, author);
                                return Flux.error(new BookNotFoundException(id));
                            }

                            statement = statement
                                    .bind("$1", uuid)
                                    .bind("$2", name)
                                    .add();
                        }
                        return Flux.from(statement.execute());
                    }).flatMap(Result::getRowsUpdated)
                    .onErrorMap(R2dbcDataIntegrityViolationException.class, this::translateException)
                    .then();
        });
    }

    /**
     * Decodes io.r2dbc.postgresql.ExceptionFactory.PostgresqlDataIntegrityViolationException
     * The pattern of detail field is
     * <p>
     * Key ({primaryKeyName})=({UUID}) is not present in table "{tableName}".
     * </p>
     *
     * @see {@link ErrorDetails}
     */
    private BookNotFoundException translateException(R2dbcDataIntegrityViolationException err) {

        try {
            Method getErrorDetailsMethod = err.getClass().getDeclaredMethod("getErrorDetails");
            getErrorDetailsMethod.setAccessible(true); // class is package private

            ErrorDetails errorDetails = (ErrorDetails) getErrorDetailsMethod.invoke(err);

            String detail = errorDetails.getDetail().orElse(null);
            if (detail == null) {
                log.debug("ErrorDetails.getDetail() was empty");
                return DEFAULT_EXCEPTION;
            }

            int start = detail.lastIndexOf('(');
            int end = detail.lastIndexOf(')');
            String bookId = detail.substring(start + 1, end);

            return new BookNotFoundException(bookId);
        } catch (NoSuchMethodException e) {
            log.warn("getErrorDetails() is not present in exception class", e);
            return DEFAULT_EXCEPTION;
        } catch (InvocationTargetException e) {
            log.warn("getErrorDetails() threw an exception", e);
            return DEFAULT_EXCEPTION;
        } catch (IllegalAccessException e) {
            log.warn("", e);
            return DEFAULT_EXCEPTION;
        }
    }
}
