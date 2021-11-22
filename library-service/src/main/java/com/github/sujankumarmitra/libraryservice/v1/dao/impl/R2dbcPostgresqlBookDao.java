package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.TagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcTag;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlBookDao implements BookDao {

    public static final String INSERT_STATEMENT = "INSERT INTO books (title,publisher,edition) values ($1,$2,$3) RETURNING id";
    public static final String DELETE_STATEMENT = "DELETE FROM books WHERE id=$1";
    public static final String SELECT_STATEMENT = "SELECT title,publisher,edition FROM books WHERE id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE books SET title=$1, publisher=$2, edition=$3 WHERE id=$4";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final AuthorDao authorDao;
    @NonNull
    private final TagDao tagDao;

    @Override
    public Mono<String> insertBook(Book book) {
        return Mono.defer(() -> {
            if (book == null) {
                log.debug("book is null");
                return Mono.error(new NullPointerException("book is null"));
            }
            R2dbcBook r2dbcBook = buildR2dbcBook(book);

            return this.databaseClient
                    .sql(INSERT_STATEMENT)
                    .bind("$1", r2dbcBook.getTitle())
                    .bind("$2", r2dbcBook.getPublisher())
                    .bind("$3", r2dbcBook.getEdition())
                    .map(row -> row.get("id", UUID.class))
                    .one()
                    .doOnNext(bookId -> log.debug("New bookId : {}", bookId))
                    .doOnNext(bookId -> setBookIds(bookId, r2dbcBook))
                    .flatMap(bookId -> authorDao
                            .insertAuthors(r2dbcBook.getAuthors())
                            .thenReturn(bookId))
                    .flatMap(bookId -> tagDao
                            .insertTags(r2dbcBook.getTags())
                            .thenReturn(bookId))
                    .map(Object::toString);

        });
    }

    @Override
    public Mono<Void> updateBook(Book book) {
        return Mono.defer(() -> {
            if (book == null) {
                log.debug("given book is null");
                return Mono.error(new NullPointerException("given book is null"));
            }
            String id = book.getId();
            if (id == null) {
                log.debug("given bookId is null");
                return Mono.error(new NullPointerException("given bookId is null"));
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException ex) {
                log.debug("given Book.getId() is not a valid uuid");
                return Mono.empty();
            }

            return select(uuid)
                    .switchIfEmpty(Mono.fromRunnable(() -> log.debug("book does not exist for id {}", uuid)))
                    .map(r2dbcBook -> applyUpdates(book, r2dbcBook))
                    .flatMap(this::updateR2dbcBook)
                    .flatMap(r2dbcBook -> authorDao
                            .updateAuthors(r2dbcBook.getAuthors())
                            .thenReturn(r2dbcBook))
                    .flatMap(r2dbcBook -> tagDao
                            .updateTags(r2dbcBook.getTags())
                            .thenReturn(r2dbcBook))
                    .then();
        });
    }

    private Mono<R2dbcBook> updateR2dbcBook(R2dbcBook book) {
        log.debug("Saving updates to db");
        return this.databaseClient
                .sql(UPDATE_STATEMENT)
                .bind("$1", book.getTitle())
                .bind("$2", book.getPublisher())
                .bind("$3", book.getEdition())
                .bind("$4", book.getUuid())
                .fetch()
                .rowsUpdated()
                .thenReturn(book);
    }

    private R2dbcBook applyUpdates(Book oldBook, R2dbcBook newBook) {
        if (oldBook.getTitle() != null)
            newBook.setTitle(oldBook.getTitle());

        if (oldBook.getPublisher() != null)
            newBook.setPublisher(oldBook.getPublisher());

        if (oldBook.getEdition() != null)
            newBook.setEdition(oldBook.getEdition());

        if (oldBook.getAuthors() != null) {
            for (Author author : oldBook.getAuthors()) {
                newBook.getAuthors().add(new R2dbcAuthor(author));
            }
        }

        if (oldBook.getTags() != null) {
            for (Tag tag : oldBook.getTags()) {
                newBook.getTags().add(new R2dbcTag(tag));
            }
        }

        return newBook;
    }


    Mono<R2dbcBook> select(UUID bookId) {
        return this.databaseClient
                .sql(SELECT_STATEMENT)
                .bind("$1", bookId)
                .map(this::mapToR2dbcBook)
                .one()
                .doOnNext(book -> book.setId(bookId));

    }

    private R2dbcBook mapToR2dbcBook(Row row, RowMetadata rowMetadata) {
        R2dbcBook book = new R2dbcBook();

        book.setTitle(row.get("title", String.class));
        book.setEdition(row.get("edition", String.class));
        book.setPublisher(row.get("publisher", String.class));

        return book;
    }

    @Override
    public Mono<Void> deleteBook(@NonNull String bookId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(bookId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not a valid uuid, returning empty Mono", bookId);
                return Mono.empty();
            }

            return this.databaseClient
                    .sql(DELETE_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnNext(updateCount -> {
                        if (updateCount == 1)
                            log.debug("Deleted book of id {}", bookId);
                        else
                            log.debug("No book with id {} present in DB", bookId);
                    }).then();
        });
    }

    private void setBookIds(UUID bookId, R2dbcBook book) {
        log.debug("Setting bookIds in R2dbcBook fields");

        book.setId(bookId);
        book.getAuthors().forEach(author -> author.setBookId(bookId));
        book.getTags().forEach(tag -> tag.setBookId(bookId));
    }

    private R2dbcBook buildR2dbcBook(Book book) {
        if (R2dbcBook.class.isAssignableFrom(book.getClass())) {
            log.debug("Supplied book already assignable R2dbcBook, returning that");
            return (R2dbcBook) book;
        }
        log.debug("Creating R2dbcBook");
        return new R2dbcBook(book);
    }


}
