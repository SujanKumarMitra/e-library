package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.BookAuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlBookDao implements BookDao<Book> {

    public static final String INSERT_STATEMENT = "INSERT INTO books (library_id,title,publisher,edition,cover_page_image_asset_id) values ($1,$2,$3,$4,$5) RETURNING id";
    public static final String DELETE_STATEMENT = "DELETE FROM books WHERE id=$1";
    public static final String SELECT_STATEMENT = "SELECT library_id,title,publisher,edition,cover_page_image_asset_id FROM books WHERE id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE books SET library_id = $1, title=$2, publisher=$3, edition=$4, cover_page_image_asset_id=$5 WHERE id=$6";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final BookAuthorDao bookAuthorDao;
    @NonNull
    private final BookTagDao bookTagDao;

    private R2dbcBook assembleResult(Tuple3<R2dbcBook, Set<BookAuthor>, Set<BookTag>> tuple3) {
        R2dbcBook book = tuple3.getT1();
        Set<BookAuthor> bookAuthors = tuple3.getT2();
        Set<BookTag> tags = tuple3.getT3();

        book.addAllAuthors(bookAuthors);
        book.addAllTags(tags);

        return book;
    }

    @Override
    @Transactional
    public Mono<String> createBook(Book book) {
        return Mono.defer(() -> {
            if (book == null) {
                log.debug("book is null");
                return Mono.error(new NullPointerException("book is null"));
            }
            R2dbcBook r2dbcBook = buildR2dbcBook(book);

            GenericExecuteSpec executeSpec = this.databaseClient.sql(INSERT_STATEMENT);

            executeSpec = executeSpec
                    .bind("$1", r2dbcBook.getLibraryId())
                    .bind("$2", r2dbcBook.getTitle())
                    .bind("$3", r2dbcBook.getPublisher())
                    .bind("$4", r2dbcBook.getEdition());

            if (r2dbcBook.getCoverPageImageAssetId() == null) {
                executeSpec = executeSpec.bindNull("$5", String.class);
            } else {
                executeSpec = executeSpec.bind("$5", r2dbcBook.getCoverPageImageAssetId());
            }
            return executeSpec
                    .map(row -> row.get("id", UUID.class))
                    .one()
                    .doOnNext(bookId -> log.debug("New bookId : {}", bookId))
                    .doOnNext(bookId -> setBookIds(bookId, r2dbcBook))
                    .flatMap(bookId -> bookAuthorDao
                            .createAuthors(r2dbcBook.getAuthors())
                            .then()
                            .thenReturn(bookId))
                    .flatMap(bookId -> bookTagDao
                            .createTags(r2dbcBook.getTags())
                            .then()
                            .thenReturn(bookId))
                    .map(Object::toString);

        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Book> getBook(String bookId) {
        return Mono.defer(() -> {
            if (bookId == null) {
                log.debug("bookId is null");
                return Mono.error(new NullPointerException("bookId can't be null"));
            }

            UUID id;
            try {
                id = UUID.fromString(bookId);
            } catch (IllegalArgumentException ex) {
                log.debug("given bookId is not valid uuid");
                return Mono.empty();
            }

            UUID finalId = id;
            Mono<R2dbcBook> book = databaseClient
                    .sql(SELECT_STATEMENT)
                    .bind("$1", id)
                    .map(this::mapToR2dbcBook)
                    .one()
                    .doOnNext(r2dbcBook -> r2dbcBook.setId(finalId));

            Mono<Set<BookAuthor>> authors = bookAuthorDao
                    .getAuthorsByBookId(bookId)
                    .collect(Collectors.toCollection(HashSet::new));

            Mono<Set<BookTag>> tags = bookTagDao
                    .getTagsByBookId(bookId)
                    .collect(Collectors.toCollection(HashSet::new));

            return Mono.zip(book, authors, tags)
                    .map(this::assembleResult);

        });
    }

    @Override
    @Transactional
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
                    .then(Mono.defer(() -> {
                        if (book.getAuthors() == null) {
                            log.debug("Book.getAuthors() is null, no changes made to authors of bookId, {}", uuid);
                            return Mono.empty();
                        } else {
                            return bookAuthorDao
                                    .deleteAuthorsByBookId(id)
                                    .thenMany(bookAuthorDao.createAuthors(book.getAuthors()))
                                    .then();
                        }
                    }).then(Mono.defer(() -> {
                        if (book.getTags() == null) {
                            log.debug("Book.getTags() is null, no changes made to tags of bookId, {}", uuid);
                            return Mono.empty();
                        } else {
                            return bookTagDao
                                    .deleteTagsByBookId(id)
                                    .thenMany(bookTagDao.createTags(book.getTags()))
                                    .then();
                        }
                    })));
        });
    }

    private Mono<Void> updateR2dbcBook(R2dbcBook book) {
        log.debug("Saving updates to db");
        GenericExecuteSpec executeSpec = this.databaseClient
                .sql(UPDATE_STATEMENT)
                .bind("$1", book.getLibraryId())
                .bind("$2", book.getTitle())
                .bind("$3", book.getPublisher())
                .bind("$4", book.getEdition());

        if (book.getCoverPageImageAssetId() == null)
            executeSpec = executeSpec.bindNull("$5", String.class);
        else
            executeSpec = executeSpec.bind("$5", book.getCoverPageImageAssetId());

        return executeSpec
                .bind("$6", book.getUuid())
                .fetch()
                .rowsUpdated()
                .then();
    }

    private R2dbcBook applyUpdates(Book oldBook, R2dbcBook newBook) {

        if (oldBook.getLibraryId() != null)
            newBook.setLibraryId(oldBook.getLibraryId());

        if (oldBook.getTitle() != null)
            newBook.setTitle(oldBook.getTitle());

        if (oldBook.getPublisher() != null)
            newBook.setPublisher(oldBook.getPublisher());

        if (oldBook.getEdition() != null)
            newBook.setEdition(oldBook.getEdition());

        if (oldBook.getCoverPageImageAssetId() != null)
            newBook.setCoverPageImageAssetId(oldBook.getCoverPageImageAssetId());

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

        book.setLibraryId(row.get("library_id", String.class));
        book.setTitle(row.get("title", String.class));
        book.setEdition(row.get("edition", String.class));
        book.setPublisher(row.get("publisher", String.class));
        book.setCoverPageImageAssetId(row.get("cover_page_image_asset_id", String.class));

        return book;
    }

    @Override
    @Transactional
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
