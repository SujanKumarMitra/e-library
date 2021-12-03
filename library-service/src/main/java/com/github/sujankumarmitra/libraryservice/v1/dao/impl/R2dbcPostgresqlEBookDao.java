package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBook;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlEBookDao implements EBookDao {

    public static final String INSERT_STATEMENT = "INSERT INTO ebooks(book_id, format) VALUES ($1,$2)";
    public static final String JOINED_SELECT_STATEMENT = "SELECT b.id, b.title, b.publisher, b.edition, b.cover_page_image_asset_id, eb.format FROM books b INNER JOIN ebooks eb ON (eb.book_id=b.id AND eb.book_id=$1)";
    public static final String SELECT_STATEMENT = "SELECT eb.book_id, eb.format FROM ebooks eb WHERE eb.book_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE ebooks SET format=$1 WHERE book_id=$2";
    public static final String DELETE_STATEMENT = "DELETE FROM ebooks WHERE book_id=$1";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final BookDao<Book> bookDao;
    @NonNull
    private final AuthorDao authorDao;
    @NonNull
    private final BookTagDao tagDao;

    @Override
    public Mono<String> createBook(EBook book) {
        return Mono.defer(() -> {

            if (book == null) {
                log.debug("null passed as parameter. Returning Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("book can't be null"));
            }


            return bookDao.createBook(book)
                    .flatMap(insertedBookId -> databaseClient
                            .sql(INSERT_STATEMENT)
                            .bind("$1", UUID.fromString(insertedBookId))
                            .bind("$2", book.getFormat().toString())
                            .fetch()
                            .rowsUpdated()
                            .thenReturn(insertedBookId));
        });
    }

    @Override
    public Mono<EBook> getBook(String bookId) {

        return Mono.defer(() -> {

            if (bookId == null) {
                log.debug("given bookId is null");
                return Mono.error(new NullPointerException("param bookId is null"));
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(bookId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid, returning empty Mono", bookId);
                return Mono.empty();
            }


            Mono<R2dbcEBook> bookMono = databaseClient
                    .sql(JOINED_SELECT_STATEMENT)
                    .bind("$1", uuid)
                    .map(row -> mapToR2dbcEBook(row, true))
                    .one();

            Mono<Set<Author>> authorsMono = authorDao
                    .getAuthorsByBookId(uuid.toString())
                    .collect(Collectors.toCollection(HashSet::new));

            Mono<Set<BookTag>> tagsMono = tagDao
                    .getTagsByBookId(uuid.toString())
                    .collect(Collectors.toCollection(HashSet::new));

            return Mono.zip(bookMono, authorsMono, tagsMono)
                    .map(this::assembleEBook)
                    .cast(EBook.class);
        });

    }

    private R2dbcEBook assembleEBook(Tuple3<R2dbcEBook, Set<Author>, Set<BookTag>> tuple3) {
        R2dbcEBook book = tuple3.getT1();
        Set<Author> authors = tuple3.getT2();
        Set<BookTag> tags = tuple3.getT3();

        book.addAllAuthors(authors);
        book.addAllTags(tags);

        return book;
    }

    protected R2dbcEBook mapToR2dbcEBook(Row row, boolean joinStatement) {
        R2dbcEBook book = new R2dbcEBook();

        if (joinStatement) {
            book.setId(row.get("id", UUID.class));
            book.setTitle(row.get("title", String.class));
            book.setPublisher(row.get("publisher", String.class));
            book.setEdition(row.get("edition", String.class));
            book.setCoverPageImageAssetId(row.get("cover_page_image_asset_id", String.class));
        }

        if (!joinStatement) {
            book.setId(row.get("book_id", UUID.class));
        }

        book.setFormat(EBookFormat.valueOf(row.get("format", String.class)));
        return book;
    }

    @Override
    public Mono<Void> updateBook(EBook book) {
        return Mono.defer(() -> {
            if (book == null) {
                log.debug("given book is null, returning Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("book can't be null"));
            }

            String id = book.getId();
            if (id == null) {
                log.debug("bookId is null, returning Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("bookId can't be null"));
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not a valid uuid, returning empty Mono", id);
                return Mono.empty();
            }

            return bookDao.updateBook(book)
                    .then(Mono.defer(() -> select(uuid)))
                    .doOnSuccess(fetchedBook -> applyUpdates(fetchedBook, book))
                    .flatMap(updatedBook -> databaseClient
                            .sql(UPDATE_STATEMENT)
                            .bind("$1", updatedBook.getFormat())
                            .bind("$2", uuid)
                            .fetch()
                            .rowsUpdated())
                    .then();
        });
    }

    private void applyUpdates(R2dbcEBook dbBook, EBook book) {
        if (book.getFormat() != null) {
            dbBook.setFormat(book.getFormat());
        }

    }

    private Mono<R2dbcEBook> select(UUID id) {
        return databaseClient
                .sql(SELECT_STATEMENT)
                .bind("$1", id)
                .map(row -> mapToR2dbcEBook(row, false))
                .one();
    }

    @Override
    public Mono<Void> deleteBook(String bookId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(bookId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not a valid uuid, return empty Mono", bookId);
                return Mono.empty();
            }
            return this.databaseClient
                    .sql(DELETE_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .then(bookDao.deleteBook(bookId));
        });
    }
}
