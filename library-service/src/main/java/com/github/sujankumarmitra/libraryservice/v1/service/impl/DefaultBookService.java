package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.InsufficientCopiesAvailableException;
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
    private final EBookPermissionService eBookPermissionService;

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
    public Mono<Void> onLeaseAccept(AcceptedLease acceptedLease) {
        return leaseRequestDao
                .getLeaseRequest(acceptedLease.getLeaseRequestId())
                .flatMap(leaseRequest -> getBook(leaseRequest.getBookId())
                        .map(book -> Tuples.of(leaseRequest, acceptedLease, book)))
                .flatMap(this::handleLeaseAcceptForBook);
    }

    @Override
    public Mono<Void> onLeaseRelinquish(@NotNull LeaseRecord leaseRecord) {
        return leaseRequestDao
                .getLeaseRequest(leaseRecord.getLeaseRequestId())
                .map(LeaseRequest::getBookId)
                .flatMap(this::getBook)
                .flatMap(this::handleLeaseRelinquishForBook);
    }

    private Mono<Void> handleLeaseRelinquishForBook(Book book) {
        if (book instanceof PhysicalBook) {
            return physicalBookDao.incrementCopiesAvailable(book.getId());
        } else if (book instanceof EBook) {
            // nothing to do for ebooks
            return Mono.empty();
        } else {
            // this should not happen
            String type = book.getClass().getSimpleName();
            log.warn("Unknown book type {}", type);
            return Mono.error(new RuntimeException("could not determine book type " + type));
        }
    }

    private Mono<Void> handleLeaseAcceptForBook(Tuple3<LeaseRequest, AcceptedLease, Book> tuple3) {
        LeaseRequest leaseRequest = tuple3.getT1();
        AcceptedLease acceptedLease = tuple3.getT2();
        Book book = tuple3.getT3();

        String bookId = book.getId();
        if (book instanceof PhysicalBook) {
            if (((PhysicalBook) book).getCopiesAvailable() > 0)
                return physicalBookDao.decrementCopiesAvailable(bookId);
            else
                return Mono.error(new InsufficientCopiesAvailableException(bookId));
        } else if (book instanceof EBook) {
            DefaultEBookPermission permission = new DefaultEBookPermission();

            permission.setBookId(bookId);
            permission.setUserId(leaseRequest.getUserId());
            permission.setStartTime(acceptedLease.getStartTimeInEpochMilliseconds());
            permission.setEndTime(acceptedLease.getDurationInMilliseconds());

            return eBookPermissionService
                    .assignPermission(permission);
        } else {
            // this should not happen
            log.warn("failed to determine book type {}", book);
            return Mono.error(new RuntimeException("could not determine book type :" + book.getClass().getCanonicalName()));
        }
    }
}
