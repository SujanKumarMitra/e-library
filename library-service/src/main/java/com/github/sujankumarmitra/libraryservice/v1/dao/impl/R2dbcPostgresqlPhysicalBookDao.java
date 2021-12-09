package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcMoney;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.InsufficientCopiesAvailableException;
import com.github.sujankumarmitra.libraryservice.v1.exception.NegativeMoneyAmountException;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.math.BigDecimal;
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
public class R2dbcPostgresqlPhysicalBookDao implements PhysicalBookDao {

    public static final String INSERT_STATEMENT = "INSERT INTO physical_books(book_id, copies_available, fine_amount, fine_currency_code) VALUES ($1,$2,$3,$4)";
    public static final String JOINED_SELECT_STATEMENT = "SELECT b.id, b.title, b.publisher, b.edition, b.cover_page_image_asset_id, pb.copies_available, pb.fine_amount, pb.fine_currency_code FROM books b INNER JOIN physical_books pb ON (pb.book_id=b.id AND pb.book_id=$1)";
    public static final String SELECT_STATEMENT = "SELECT pb.book_id, pb.copies_available, pb.fine_amount, pb.fine_currency_code FROM physical_books pb WHERE pb.book_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE physical_books SET copies_available=$1, fine_amount=$2, fine_currency_code=$3 WHERE book_id=$4";
    public static final String DELETE_STATEMENT = "DELETE FROM physical_books WHERE book_id=$1";
    public static final String DECREMENT_COPIES_AVAILABLE_STATEMENT = "UPDATE physical_books SET copies_available=copies_available-1 WHERE book_id=$1";
    public static final String INCREMENT_COPIES_AVAILABLE_STATEMENT = "UPDATE physical_books SET copies_available=copies_available+1 WHERE book_id=$1";
    public static final String POSITIVE_COPIES_AVAILABLE_CONSTRAINT_NAME = "chk_physical_book_copies_positive";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final BookDao<Book> bookDao;
    @NonNull
    private final AuthorDao authorDao;
    @NonNull
    private final BookTagDao tagDao;

    @Override
    @Transactional
    public Mono<String> createBook(PhysicalBook book) {
        return Mono.defer(() -> {

            if (book == null) {
                log.debug("null passed as parameter. Returning Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("book can't be null"));
            }

            Long copiesAvailable = book.getCopiesAvailable();
            if (copiesAvailable < 0) {
                log.debug("invalid copiesAvailable {}", copiesAvailable);
                return Mono.error(new InsufficientCopiesAvailableException(book.getId()));
            }

            BigDecimal amount = book.getFinePerDay().getAmount();
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                log.debug("invalid amount {}", amount);
                return Mono.error(new NegativeMoneyAmountException(amount));
            }

            return bookDao.createBook(book)
                    .flatMap(insertedBookId -> databaseClient
                            .sql(INSERT_STATEMENT)
                            .bind("$1", UUID.fromString(insertedBookId))
                            .bind("$2", copiesAvailable)
                            .bind("$3", book.getFinePerDay().getAmount())
                            .bind("$4", book.getFinePerDay().getCurrencyCode())
                            .fetch()
                            .rowsUpdated()
                            .thenReturn(insertedBookId));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PhysicalBook> getBook(String bookId) {

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


            Mono<R2dbcPhysicalBook> bookMono = databaseClient
                    .sql(JOINED_SELECT_STATEMENT)
                    .bind("$1", uuid)
                    .map(row -> mapToR2dbcPhysicalBook(row, true))
                    .one();

            Mono<Set<Author>> authorsMono = authorDao
                    .getAuthorsByBookId(uuid.toString())
                    .collect(Collectors.toCollection(HashSet::new));

            Mono<Set<BookTag>> tagsMono = tagDao
                    .getTagsByBookId(uuid.toString())
                    .collect(Collectors.toCollection(HashSet::new));

            return Mono.zip(bookMono, authorsMono, tagsMono)
                    .map(this::assemblePhysicalBook)
                    .cast(PhysicalBook.class);
        });

    }

    private R2dbcPhysicalBook assemblePhysicalBook(Tuple3<R2dbcPhysicalBook, Set<Author>, Set<BookTag>> tuple3) {
        R2dbcPhysicalBook book = tuple3.getT1();
        Set<Author> authors = tuple3.getT2();
        Set<BookTag> tags = tuple3.getT3();

        book.addAllAuthors(authors);
        book.addAllTags(tags);

        return book;
    }

    protected R2dbcPhysicalBook mapToR2dbcPhysicalBook(Row row, boolean joinStatement) {
        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        R2dbcMoney finePerDay = new R2dbcMoney();

        book.setFinePerDay(finePerDay);


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

        book.setCopiesAvailable(row.get("copies_available", Long.class));
        finePerDay.setAmount(row.get("fine_amount", BigDecimal.class));
        finePerDay.setCurrencyCode(row.get("fine_currency_code", String.class));

        return book;
    }

    @Override
    @Transactional
    public Mono<Void> updateBook(PhysicalBook book) {
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

            Long copiesAvailable = book.getCopiesAvailable();
            if (copiesAvailable != null && copiesAvailable < 0) {
                log.debug("invalid copiesAvailable {}", copiesAvailable);
                return Mono.error(new InsufficientCopiesAvailableException(book.getId()));
            }

            Money finePerDay = book.getFinePerDay();

            if (finePerDay != null) {
                BigDecimal amount = finePerDay.getAmount();

                if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
                    log.debug("invalid amount {}", amount);
                    return Mono.error(new NegativeMoneyAmountException(amount));
                }
            }

            return bookDao.updateBook(book)
                    .then(Mono.defer(() -> select(uuid)))
                    .doOnSuccess(fetchedBook -> applyUpdates(fetchedBook, book))
                    .flatMap(updatedBook -> databaseClient
                            .sql(UPDATE_STATEMENT)
                            .bind("$1", updatedBook.getCopiesAvailable())
                            .bind("$2", updatedBook.getFinePerDay().getAmount())
                            .bind("$3", updatedBook.getFinePerDay().getCurrencyCode())
                            .bind("$4", uuid)
                            .fetch()
                            .rowsUpdated())
                    .then();
        });
    }

    private void applyUpdates(R2dbcPhysicalBook dbBook, PhysicalBook book) {
        if (book.getCopiesAvailable() != null) {
            dbBook.setCopiesAvailable(book.getCopiesAvailable());
        }

        Money finePerDay = book.getFinePerDay();
        if (finePerDay != null) {

            if (finePerDay.getAmount() != null) {
                dbBook.getFinePerDay().setAmount(finePerDay.getAmount());
            }

            if (finePerDay.getCurrencyCode() != null) {
                dbBook.getFinePerDay().setCurrencyCode(finePerDay.getCurrencyCode());
            }
        }
    }

    private Mono<R2dbcPhysicalBook> select(UUID id) {
        return databaseClient
                .sql(SELECT_STATEMENT)
                .bind("$1", id)
                .map(row -> mapToR2dbcPhysicalBook(row, false))
                .one();
    }

    @Override
    @Transactional
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


    @Override
    @Transactional
    public Mono<Void> decrementCopiesAvailable(@NonNull String bookId) {
        return Mono.defer(() -> {

            UUID id;
            try {
                id = UUID.fromString(bookId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not a valid uuid, returning empty Mono", bookId);
                return Mono.empty();
            }

            return databaseClient
                    .sql(DECREMENT_COPIES_AVAILABLE_STATEMENT)
                    .bind("$1", id)
                    .fetch()
                    .rowsUpdated()
                    .doOnNext(updateCount -> log.debug("Rows Updated {}", updateCount))
                    .then()
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateError(err, bookId));
        });
    }

    @Override
    @Transactional
    public Mono<Void> incrementCopiesAvailable(String bookId) {
        return Mono.defer(() -> {

            UUID id;
            try {
                id = UUID.fromString(bookId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not a valid uuid, returning empty Mono", bookId);
                return Mono.empty();
            }

            return databaseClient
                    .sql(INCREMENT_COPIES_AVAILABLE_STATEMENT)
                    .bind("$1", id)
                    .fetch()
                    .rowsUpdated()
                    .doOnNext(updateCount -> log.debug("Rows Updated {}", updateCount))
                    .then()
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateError(err, bookId));
        });
    }

    private Throwable translateError(DataIntegrityViolationException e, String bookId) {
        log.debug("DB integrity error", e);
        String message = e.getMessage();

        if (message != null && message.contains(POSITIVE_COPIES_AVAILABLE_CONSTRAINT_NAME))
            return new InsufficientCopiesAvailableException(bookId);

        log.debug("failed to translate error, falling back to orginal thrown error");
        return e;
    }
}
