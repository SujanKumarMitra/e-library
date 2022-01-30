package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookAuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookTag;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBook;
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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat.PDF;
import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlEBookDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    @Mock
    private BookDao<Book> mockBookDao;
    @Mock
    private BookAuthorDao mockBookAuthorDao;
    @Mock
    private BookTagDao mockBookTagDao;
    @SuppressWarnings("FieldMayBeFinal")
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;
    private final Faker faker = new Faker();
    private R2dbcPostgresqlEBookDao eBookDao;

    @BeforeEach
    void setUp() {
        eBookDao = new R2dbcPostgresqlEBookDao(
                entityTemplate.getDatabaseClient(),
                mockBookDao,
                mockBookAuthorDao,
                mockBookTagDao
        );
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidEBook_whenInsert_shouldInsert() {
        R2dbcEBook book = createBook();
        this.entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getLibraryId())
                .bind("$2", book.getTitle())
                .bind("$3", book.getPublisher())
                .bind("$4", book.getEdition())
                .bind("$5", book.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(book::setId)
                .doOnSuccess(id -> Mockito
                        .doReturn(Mono.fromSupplier(id::toString))
                        .when(mockBookDao)
                        .createBook(any()))
                .then(Mono.defer(() -> eBookDao.createBook(book)))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(insertedId -> log.info("Inserted id:: {}", insertedId))
                .verifyComplete();
    }


    @Test
    void givenValidEBookId_whenDelete_shouldDelete() {
        R2dbcEBook book = createBook();

        Mockito.doAnswer(invocation -> entityTemplate
                        .getDatabaseClient()
                        .sql("DELETE FROM books WHERE id=$1")
                        .bind("$1", UUID.fromString(invocation.getArgument(0, String.class)))
                        .fetch()
                        .rowsUpdated()
                        .then())
                .when(mockBookDao).deleteBook(any());


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
                .doOnSuccess(book::setId)
                .flatMap(id -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlEBookDao.INSERT_STATEMENT)
                        .bind("$1", id)
                        .bind("$2", book.getFormat().toString())
                        .fetch()
                        .rowsUpdated()
                        .thenReturn(id))
                .map(Object::toString)
                .flatMap(eBookDao::deleteBook)
                .then(
                        entityTemplate
                                .getDatabaseClient()
                                .sql("SELECT * from ebooks")
                                .fetch()
                                .all()
                                .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();

    }


    @Test
    void givenValidEBookId_whenGetBook_shouldGetBook() {
        R2dbcEBook expectedBook = createBook();

        Set<BookAuthor> bookAuthors = new HashSet<>();
        Set<BookTag> tags = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            R2dbcBookAuthor author = new R2dbcBookAuthor();
            author.setId(UUID.randomUUID());
            author.setName(faker.book().author());

            R2dbcBookTag bookTag = new R2dbcBookTag();
            bookTag.setId(UUID.randomUUID());
            bookTag.setKey("key" + i);
            bookTag.setValue("value" + i);

            bookAuthors.add(author);
            tags.add(bookTag);
        }

        expectedBook.addAllAuthors(bookAuthors);
        expectedBook.addAllTags(tags);

        Mockito.doReturn(Flux.fromIterable(bookAuthors))
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.fromIterable(tags))
                .when(mockBookTagDao).getTagsByBookId(any());


        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", expectedBook.getLibraryId())
                .bind("$2", expectedBook.getTitle())
                .bind("$3", expectedBook.getPublisher())
                .bind("$4", expectedBook.getEdition())
                .bind("$5", expectedBook.getCoverPageImageAssetId())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(expectedBook::setId)
                .doOnSuccess(id -> expectedBook.getAuthors().forEach(author -> author.setBookId(id)))
                .doOnSuccess(id -> expectedBook.getTags().forEach(tag -> tag.setBookId(id)))
                .flatMap(id -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlEBookDao.INSERT_STATEMENT)
                        .bind("$1", id)
                        .bind("$2", expectedBook.getFormat().toString())
                        .fetch()
                        .rowsUpdated()
                        .thenReturn(id))
                .map(Object::toString)
                .flatMap(eBookDao::getBook)
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
                .when(mockBookAuthorDao).getAuthorsByBookId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockBookTagDao).getTagsByBookId(any());

        eBookDao.getBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenDelete_shouldEmitEmpty() {

        Mockito.doReturn(Mono.empty())
                .when(mockBookDao)
                .deleteBook(any());

        eBookDao.deleteBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenMalformedBookId_whenDelete_shouldEmitEmpty() {
        eBookDao.deleteBook("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }


    private R2dbcEBook createBook() {
        R2dbcEBook eBook = new R2dbcEBook();

        com.github.javafaker.Book book = faker.book();

        eBook.setLibraryId(faker.idNumber().valid());
        eBook.setTitle(book.title());
        eBook.setPublisher(book.publisher());
        eBook.setEdition("1st");
        eBook.setCoverPageImageAssetId(faker.idNumber().valid());
        eBook.setFormat(PDF);

        return eBook;
    }
}