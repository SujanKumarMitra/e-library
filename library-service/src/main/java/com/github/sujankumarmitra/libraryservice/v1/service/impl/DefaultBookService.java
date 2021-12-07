package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultEBookPermission;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Service
@AllArgsConstructor
@Slf4j
public class DefaultBookService implements BookService {

    @NotNull
    private final PhysicalBookDao physicalBookDao;
    @NotNull
    private final EBookDao eBookDao;
    @NotNull
    private final LeaseRequestDao leaseRequestDao;
    @NotNull
    private EBookPermissionService eBookPermissionService;

    @Override
    public Mono<String> createBook(PhysicalBook book) {
        return physicalBookDao.createBook(book);
    }

    @Override
    public Mono<String> createBook(EBook book) {
        return eBookDao.createBook(book);
    }

    @Override
    public Mono<Book> getBook(String bookId) {
        return physicalBookDao
                .getBook(bookId)
                .cast(Book.class)
                .switchIfEmpty(Mono.defer(() -> eBookDao.getBook(bookId)));
    }

    @Override
    public Mono<Void> updateBook(PhysicalBook book) {
        return physicalBookDao.updateBook(book);
    }

    @Override
    public Mono<Void> updateBook(EBook book) {
        return eBookDao.updateBook(book);
    }

    @Override
    public Mono<Void> deleteBook(String bookId) {
        return Mono.when(physicalBookDao.deleteBook(bookId), eBookDao.deleteBook(bookId));
    }

    @Override
    public Mono<Void> handleLeaseAccept(AcceptedLease acceptedLease) {
        return leaseRequestDao
                .getLeaseRequest(acceptedLease.getLeaseRequestId())
                .flatMap(leaseRequest -> getBook(leaseRequest.getBookId())
                        .map(book -> Tuples.of(leaseRequest, acceptedLease, book)))
                .flatMap(this::handleLeaseAcceptForBook);
    }

    private Mono<Void> handleLeaseAcceptForBook(Tuple3<LeaseRequest, AcceptedLease, Book> tuple3) {
        LeaseRequest leaseRequest = tuple3.getT1();
        AcceptedLease acceptedLease = tuple3.getT2();
        Book book = tuple3.getT3();

        if (book instanceof PhysicalBook) {
            return physicalBookDao.decrementCopiesAvailable(book.getId());
        } else if (book instanceof EBook) {
            DefaultEBookPermission permission = new DefaultEBookPermission();

            permission.setBookId(book.getId());
            permission.setUserId(leaseRequest.getUserId());
            permission.setStartTime(acceptedLease.getStartTime());
            permission.setEndTime(acceptedLease.getEndTime());

            return eBookPermissionService
                    .assignPermission(permission);
        } else {
            // this should not happen
            log.warn("failed to determine book type {}", book);
            return Mono.error(new RuntimeException("could not determine book type :" + book.getClass().getCanonicalName()));
        }
    }
}
