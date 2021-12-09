package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LibrarianDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.LibrarianAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
public class R2dbcPostgresqlLibrarianDao implements LibrarianDao {
    public static final String PRIMARY_KEY_CONSTRAINT_NAME = "pk_librarians";
    public static final String INSERT_STATEMENT = "INSERT INTO librarians(id) VALUES ($1)";
    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional
    public Mono<Void> createLibrarian(@NonNull Librarian librarian) {
        return entityTemplate
                .getDatabaseClient()
                .sql(INSERT_STATEMENT)
                .bind("$1", librarian.getId())
                .fetch()
                .rowsUpdated()
                .then()
                .onErrorMap(DataIntegrityViolationException.class, err -> translateErrors(err, librarian));
    }

    private Throwable translateErrors(DataIntegrityViolationException ex, Librarian librarian) {
        log.debug("DB integrity error", ex);
        String message = ex.getMessage();

        if (message == null) {
            log.debug("DataIntegrityViolationException.getMessage() returned null, can't translate exception, falling back to original exception");
            return ex;
        }

        if (message.contains(PRIMARY_KEY_CONSTRAINT_NAME)) {
            return new LibrarianAlreadyExistsException(librarian.getId());
        }

        log.debug("failed to translate error, falling back to original thrown exception");
        return ex;
    }

    @Override
    @Transactional
    public Mono<Void> deleteLibrarian(@NonNull String librarianId) {
        return entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM librarians WHERE id=$1")
                .bind("$1", librarianId)
                .fetch()
                .rowsUpdated()
                .then();
    }
}
