package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookTag;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcMoney;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.InsufficientCopiesAvailableException;
import com.github.sujankumarmitra.libraryservice.v1.exception.NegativeMoneyAmountException;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlPhysicalBookDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    @Mock
    private BookDao<Book> mockBookDao;
    @Mock
    private AuthorDao mockAuthorDao;
    @Mock
    private BookTagDao mockBookTagDao;
    @SuppressWarnings("FieldMayBeFinal")
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;
    private final Faker faker = new Faker();
    private R2dbcPostgresqlPhysicalBookDao physicalBookDao;

    @BeforeEach
    void setUp() {
        physicalBookDao = new R2dbcPostgresqlPhysicalBookDao(
                entityTemplate.getDatabaseClient(),
                mockBookDao,
                mockAuthorDao,
                mockBookTagDao
        );

        Mockito.doReturn(Mono.empty())
                .when(mockBookDao).deleteBook(any());
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM physical_books")
                .fetch()
                .rowsUpdated()
                .block();


        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM books")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenValidPhysicalBook_whenInsert_shouldInsert() {
        R2dbcPhysicalBook book = createBook();
        this.entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getTitle())
                .bind("$2", book.getPublisher())
                .bind("$3", book.getEdition())
                .bind("$4", book.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(book::setId)
                .doOnSuccess(id -> Mockito
                        .doReturn(Mono.fromSupplier(id::toString))
                        .when(mockBookDao)
                        .createBook(any()))
                .then(Mono.defer(() -> physicalBookDao.createBook(book)))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(insertedId -> log.info("Inserted id:: {}", insertedId))
                .verifyComplete();
    }


    @Test
    void givenBookWithNegativeCopies_whenInsert_shouldEmitError() {
        R2dbcPhysicalBook book = createBook();
        book.setCopiesAvailable(-1L);

        physicalBookDao.createBook(book)
                .as(StepVerifier::create)
                .expectError(InsufficientCopiesAvailableException.class)
                .verify();
    }

    @Test
    void givenBookWithNegativeMoney_whenInsert_shouldEmitError() {
        R2dbcPhysicalBook book = createBook();
        book.getFinePerDay().setAmount(new BigDecimal("-10.00"));

        physicalBookDao.createBook(book)
                .as(StepVerifier::create)
                .expectError(NegativeMoneyAmountException.class)
                .verify();
    }

    @Test
    void givenBookWithNegativeCopies_whenUpdate_shouldEmitError() {
        R2dbcPhysicalBook book = createBook();
        book.setId(UUID.randomUUID());
        book.setCopiesAvailable(-1L);

        physicalBookDao.updateBook(book)
                .as(StepVerifier::create)
                .expectError(InsufficientCopiesAvailableException.class)
                .verify();
    }

    @Test
    void givenBookWithNegativeMoney_whenUpdate_shouldEmitError() {
        R2dbcPhysicalBook book = createBook();
        book.setId(UUID.randomUUID());
        book.getFinePerDay().setAmount(new BigDecimal("-10.00"));

        physicalBookDao.updateBook(book)
                .as(StepVerifier::create)
                .expectError(NegativeMoneyAmountException.class)
                .verify();
    }

    @Test
    void givenValidPhysicalBookId_whenDelete_shouldDelete() {
        R2dbcPhysicalBook book = createBook();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getTitle())
                .bind("$2", book.getPublisher())
                .bind("$3", book.getEdition())
                .bind("$4", book.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(book::setId)
                .flatMap(id -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlPhysicalBookDao.INSERT_STATEMENT)
                        .bind("$1", id)
                        .bind("$2", book.getCopiesAvailable())
                        .bind("$3", book.getFinePerDay().getAmount())
                        .bind("$4", book.getFinePerDay().getCurrencyCode())
                        .fetch()
                        .rowsUpdated()
                        .thenReturn(id))
                .map(Object::toString)
                .flatMap(physicalBookDao::deleteBook)
                .then(
                        entityTemplate
                                .getDatabaseClient()
                                .sql("SELECT * from physical_books")
                                .fetch()
                                .all()
                                .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();

    }


    @Test
    void givenValidBookId_whenGetBook_shouldGetBook() {
        R2dbcPhysicalBook expectedBook = createBook();

        Set<Author> authors = new HashSet<>();
        Set<BookTag> tags = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            R2dbcAuthor author = new R2dbcAuthor();
            author.setId(UUID.randomUUID());
            author.setName(faker.book().author());

            R2dbcBookTag bookTag = new R2dbcBookTag();
            bookTag.setId(UUID.randomUUID());
            bookTag.setKey("key" + i);
            bookTag.setValue("value" + i);

            authors.add(author);
            tags.add(bookTag);
        }

        expectedBook.addAllAuthors(authors);
        expectedBook.addAllTags(tags);

        Mockito.doReturn(Flux.fromIterable(authors))
                .when(mockAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.fromIterable(tags))
                .when(mockBookTagDao).getTagsByBookId(any());


        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", expectedBook.getTitle())
                .bind("$2", expectedBook.getPublisher())
                .bind("$3", expectedBook.getEdition())
                .bind("$4", expectedBook.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(expectedBook::setId)
                .doOnSuccess(id -> expectedBook.getAuthors().forEach(author -> author.setBookId(id)))
                .doOnSuccess(id -> expectedBook.getTags().forEach(tag -> tag.setBookId(id)))
                .flatMap(id -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlPhysicalBookDao.INSERT_STATEMENT)
                        .bind("$1", id)
                        .bind("$2", expectedBook.getCopiesAvailable())
                        .bind("$3", expectedBook.getFinePerDay().getAmount())
                        .bind("$4", expectedBook.getFinePerDay().getCurrencyCode())
                        .fetch()
                        .rowsUpdated()
                        .thenReturn(id))
                .map(Object::toString)
                .flatMap(physicalBookDao::getBook)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualBook -> {
                    log.info("Expected {}", expectedBook);
                    log.info("Actual {}", actualBook);

                    assertThat(actualBook).isEqualTo(expectedBook);
                })
                .verifyComplete();
    }

    @Test
    void givenNonExistingBook_whenGetBook_shouldEmitEmpty() {

        Mockito.doReturn(Flux.empty())
                .when(mockAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockBookTagDao).getTagsByBookId(any());

        physicalBookDao.getBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenValidBook_whenUpdate_shouldUpdate() {
        R2dbcPhysicalBook book = createBook();
        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getTitle())
                .bind("$2", book.getPublisher())
                .bind("$3", book.getEdition())
                .bind("$4", book.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(book::setId)
                .doOnSuccess(id -> book.getAuthors().forEach(author -> author.setBookId(id)))
                .doOnSuccess(id -> book.getTags().forEach(tag -> tag.setBookId(id)))
                .flatMap(id -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlPhysicalBookDao.INSERT_STATEMENT)
                        .bind("$1", id)
                        .bind("$2", book.getCopiesAvailable())
                        .bind("$3", book.getFinePerDay().getAmount())
                        .bind("$4", book.getFinePerDay().getCurrencyCode())
                        .fetch()
                        .rowsUpdated()
                        .thenReturn(id))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1L)
                .verifyComplete();


        book.setCopiesAvailable(20L);
        book.getFinePerDay().setAmount(new BigDecimal("10.00"));

        book.setAuthors(null);
        book.setTags(null);

        Mockito.doReturn(Mono.empty())
                .when(mockBookDao).updateBook(any());

        physicalBookDao.updateBook(book)
                .then(Mono.defer(() ->
                        entityTemplate
                                .getDatabaseClient()
                                .sql(R2dbcPostgresqlPhysicalBookDao.JOINED_SELECT_STATEMENT)
                                .bind("$1", UUID.fromString(Objects.requireNonNull(book.getId())))
                                .map(row -> physicalBookDao.mapToR2dbcPhysicalBook(row, true))
                                .one()))
                .doOnSuccess(fetchedBook -> fetchedBook.setAuthors(null))
                .doOnSuccess(fetchedBook -> fetchedBook.setTags(null))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualBook -> {
                    log.info("Expected book: {}", book);
                    log.info("Expected book: {}", actualBook);

                    assertThat(actualBook).isEqualTo(book);
                })
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenDelete_shouldEmitEmpty() {

        physicalBookDao.deleteBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenMalformedBookId_whenDelete_shouldEmitEmpty() {
        physicalBookDao.deleteBook("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }


    private R2dbcPhysicalBook createBook() {
        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();

        com.github.javafaker.Book book = faker.book();

        physicalBook.setTitle(book.title());
        physicalBook.setPublisher(book.publisher());
        physicalBook.setEdition("1st");
        physicalBook.setCoverPageImageAssetId(faker.idNumber().valid());
        physicalBook.setCopiesAvailable(10L);

        R2dbcMoney money = new R2dbcMoney();
        money.setAmount(new BigDecimal("100.00"));
        money.setCurrencyCode("INR");
        physicalBook.setFinePerDay(money);

        return physicalBook;
    }
}