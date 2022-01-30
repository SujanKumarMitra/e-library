package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookAuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookTag;
import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.springframework.r2dbc.connection.init.ScriptUtils.executeSqlScript;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlBookDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    private R2dbcPostgresqlBookDao bookDao;
    @Mock
    private BookTagDao mockBookTagDao = null;
    @Mock
    private BookAuthorDao mockBookAuthorDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    private final Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        Mockito.doReturn(Flux.empty())
                .when(mockBookAuthorDao).createAuthors(anySet());

        Mockito.doReturn(Flux.empty())
                .when(mockBookTagDao).createTags(anySet());

        Mockito.doReturn(Mono.empty())
                .when(mockBookTagDao).deleteTagsByBookId(any());

        Mockito.doReturn(Mono.empty())
                .when(mockBookAuthorDao).deleteAuthorsByBookId(any());


        bookDao = new R2dbcPostgresqlBookDao(
                entityTemplate.getDatabaseClient(),
                mockBookAuthorDao,
                mockBookTagDao);
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidBook_whenInsert_shouldInsert() {
        Book book = getBook();
        bookDao.createBook(book)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(log::info)
                .verifyComplete();
    }

    @Test
    void givenValidBookWithNullCoverImageId_whenInsert_shouldHandleNull() {
        R2dbcBook book = getBook();
        book.setCoverPageImageAssetId(null);

        bookDao.createBook(book)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(log::info)
                .verifyComplete();
    }

    @Test
    void givenValidBookWithNullCoverImagePage_whenSelect_shouldHandleNull() {

        R2dbcBook book = getBook();
        book.setCoverPageImageAssetId(null);

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getLibraryId())
                .bind("$2", book.getTitle())
                .bind("$3", book.getPublisher())
                .bind("$4", book.getEdition())
                .bindNull("$5", String.class)
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnNext(book::setId)
                .then()
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();


        log.info("Inserted book {}", book);

        String expectedId = book.getId();
        String expectedTitle = book.getTitle();
        String expectedPublisher = book.getPublisher();
        String expectedEdition = book.getEdition();

        R2dbcBookAuthor author1 = new R2dbcBookAuthor();
        author1.setBookId(book.getUuid());
        author1.setName(faker.book().author());

        R2dbcBookAuthor author2 = new R2dbcBookAuthor();
        author1.setBookId(book.getUuid());
        author1.setName(faker.book().author());

        Set<R2dbcBookAuthor> expectedAuthors = Set.of(author1, author2);

        R2dbcBookTag tag1 = new R2dbcBookTag();
        tag1.setBookId(book.getUuid());
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcBookTag tag2 = new R2dbcBookTag();
        tag2.setBookId(book.getUuid());
        tag2.setKey("key2");
        tag2.setValue("value2");

        Set<R2dbcBookTag> expectedTags = Set.of(tag1, tag2);


        book.getAuthors().addAll(expectedAuthors);
        book.getTags().addAll(expectedTags);


        Mockito.doReturn(Flux.fromIterable(expectedAuthors).cast(BookAuthor.class))
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.fromIterable(expectedTags).cast(BookTag.class))
                .when(mockBookTagDao).getTagsByBookId(any());

        log.info("Expected book:: {}", book);

        bookDao.getBook(book.getId())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualBook -> {
                    log.info("Actual book:: {}", actualBook);
                    assertThat(actualBook.getId()).isEqualTo(expectedId);
                    assertThat(actualBook.getTitle()).isEqualTo(expectedTitle);
                    assertThat(actualBook.getPublisher()).isEqualTo(expectedPublisher);
                    assertThat(actualBook.getCoverPageImageAssetId()).isNull();
                    assertThat(actualBook.getEdition()).isEqualTo(expectedEdition);
                    assertThat(actualBook.getAuthors()).isEqualTo(expectedAuthors);
                    assertThat(actualBook.getTags()).isEqualTo(expectedTags);
                })
                .verifyComplete();


    }

    @Test
    void givenValidBookId_whenSelect_shouldSelect() {

        R2dbcBook book = getBook();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getLibraryId())
                .bind("$2", book.getTitle())
                .bind("$3", book.getPublisher())
                .bind("$4", book.getEdition())
                .bind("$5", book.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnNext(book::setId)
                .then()
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();


        log.info("Inserted book {}", book);

        String expectedId = book.getId();
        String expectedTitle = book.getTitle();
        String expectedPublisher = book.getPublisher();
        String expectedEdition = book.getEdition();
        String expectedCoverPageImageId = book.getCoverPageImageAssetId();

        R2dbcBookAuthor author1 = new R2dbcBookAuthor();
        author1.setBookId(book.getUuid());
        author1.setName(faker.book().author());

        R2dbcBookAuthor author2 = new R2dbcBookAuthor();
        author1.setBookId(book.getUuid());
        author1.setName(faker.book().author());

        Set<R2dbcBookAuthor> expectedAuthors = Set.of(author1, author2);

        R2dbcBookTag tag1 = new R2dbcBookTag();
        tag1.setBookId(book.getUuid());
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcBookTag tag2 = new R2dbcBookTag();
        tag2.setBookId(book.getUuid());
        tag2.setKey("key2");
        tag2.setValue("value2");

        Set<R2dbcBookTag> expectedTags = Set.of(tag1, tag2);


        book.getAuthors().addAll(expectedAuthors);
        book.getTags().addAll(expectedTags);


        Mockito.doReturn(Flux.fromIterable(expectedAuthors).cast(BookAuthor.class))
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.fromIterable(expectedTags).cast(BookTag.class))
                .when(mockBookTagDao).getTagsByBookId(any());

        log.info("Expected book:: {}", book);

        bookDao.getBook(book.getId())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualBook -> {
                    log.info("Actual book:: {}", actualBook);
                    assertThat(actualBook.getId()).isEqualTo(expectedId);
                    assertThat(actualBook.getTitle()).isEqualTo(expectedTitle);
                    assertThat(actualBook.getPublisher()).isEqualTo(expectedPublisher);
                    assertThat(actualBook.getEdition()).isEqualTo(expectedEdition);
                    assertThat(actualBook.getCoverPageImageAssetId()).isEqualTo(expectedCoverPageImageId);
                    assertThat(actualBook.getAuthors()).isEqualTo(expectedAuthors);
                    assertThat(actualBook.getTags()).isEqualTo(expectedTags);
                })
                .verifyComplete();


    }

    @Test
    void givenInvalidBookId_whenSelect_shouldEmitNothing() {
        Mockito.doReturn(Flux.empty())
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockBookTagDao).getTagsByBookId(any());

        bookDao.getBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedBookId_whenSelect_shouldEmitNothing() {
        Mockito.doReturn(Flux.empty())
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockBookTagDao).getTagsByBookId(any());

        bookDao.getBook("malformed_uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenValidBook_whenUpdated_shouldUpdate() {

        R2dbcBook book = getBook();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getLibraryId())
                .bind("$2", book.getTitle())
                .bind("$3", book.getPublisher())
                .bind("$4", book.getEdition())
                .bind("$5", book.getCoverPageImageAssetId())
                .fetch()
                .one()
                .map(map -> map.get("id"))
                .cast(UUID.class)
                .doOnNext(book::setId)
                .then()
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

        log.info("Inserted book:: {}", book);

        String expectedTitle = faker.book().title();
        String expectedPublisher = faker.book().publisher();
        String expectedEdition = "2nd";
        String expectedCoverPageImageId = faker.idNumber().valid();

        book.setTitle(expectedTitle);
        book.setPublisher(expectedPublisher);
        book.setEdition(expectedEdition);
        book.setCoverPageImageAssetId(expectedCoverPageImageId);

        log.info("Expected book:: {}", book);

        bookDao.updateBook(book)
                .then(Mono.defer(() -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlBookDao.SELECT_STATEMENT)
                        .bind("$1", book.getUuid())
                        .map(row -> {
                            R2dbcBook actualBook = new R2dbcBook();

                            actualBook.setId(book.getUuid());
                            actualBook.setTitle(row.get("title", String.class));
                            actualBook.setPublisher(row.get("publisher", String.class));
                            actualBook.setEdition(row.get("edition", String.class));
                            actualBook.setCoverPageImageAssetId(row.get("cover_page_image_asset_id", String.class));

                            return actualBook;
                        })
                        .one()
                        .switchIfEmpty(Mono.error(new RuntimeException("no rows fetched")))))
                .as(StepVerifier::create)
                .consumeNextWith(actualBook -> {
                    log.info("Actual book:: {} ", actualBook);

                    String actualTitle = actualBook.getTitle();
                    String actualPublisher = actualBook.getPublisher();
                    String actualEdition = actualBook.getEdition();
                    String actualCoverPageImageId = actualBook.getCoverPageImageAssetId();

                    assertThat(actualTitle).isEqualTo(expectedTitle);
                    assertThat(actualEdition).isEqualTo(expectedEdition);
                    assertThat(actualPublisher).isEqualTo(expectedPublisher);
                    assertThat(actualCoverPageImageId).isEqualTo(expectedCoverPageImageId);

                }).verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenUpdate_shouldEmitComplete() {
        R2dbcBook book = getBook();
        book.setId(UUID.randomUUID());

        bookDao.updateBook(book)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenValidBookId_whenDelete_shouldDelete() {
        entityTemplate.getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", faker.idNumber().valid())
                .bind("$2", faker.book().title())
                .bind("$3", faker.book().publisher())
                .bind("$4", "1st")
                .bind("$5", faker.idNumber().valid())
                .fetch()
                .one()
                .map(map -> map.get("id"))
                .cast(UUID.class)
                .map(Object::toString)
                .flatMap(bookDao::deleteBook)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

        entityTemplate.getDatabaseClient()
                .sql("SELECT * FROM books")
                .fetch()
                .all()
                .count()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenInvalidId_whenDelete_shouldEmitEmpty() {
        bookDao.deleteBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenDelete_shouldEmitEmpty() {
        bookDao.deleteBook("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenValidBookId_whenDelete_shouldDeleteBookAndItsDependentEntities() {
        UUID validBookId = UUID.fromString("d4c608c4-7ac6-48b9-b90d-9952e798578b");

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, new ClassPathResource("sample_data.sql")))
                .then(bookDao.deleteBook(validBookId.toString()))
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT COUNT(*) FROM books WHERE id=$1")
                        .bind("$1", validBookId)
                        .map(row -> row.get(0, Integer.class))
                        .one())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();

        entityTemplate
                .getDatabaseClient()
                .sql("SELECT COUNT(*) from authors WHERE book_id=$1")
                .bind("$1", validBookId)
                .map(row -> row.get(0, Integer.class))
                .one()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();

        entityTemplate
                .getDatabaseClient()
                .sql("SELECT COUNT(*) from book_tags WHERE book_id=$1")
                .bind("$1", validBookId)
                .map(row -> row.get(0, Integer.class))
                .one()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();

    }

    private R2dbcBook getBook() {

        R2dbcBook book = new R2dbcBook();

        book.setLibraryId(faker.idNumber().valid());
        book.setTitle(faker.book().title());
        book.setPublisher(faker.book().publisher());
        book.setEdition("1st");
        book.setCoverPageImageAssetId(faker.idNumber().valid());

        return book;

    }
}