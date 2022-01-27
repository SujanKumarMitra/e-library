package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import com.github.sujankumarmitra.libraryservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_LIBRARIAN;
import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_STUDENT;

/**
 * @author skmitra
 * @since Jan 26/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredBookService implements BookService {

    @NonNull
    private final BookService delegate;

    @Override
    @PreAuthorize("hasAuthority(#book.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<String> createBook(PhysicalBook book) {
        return delegate.createBook(book);
    }

    @Override
    @PreAuthorize("hasAuthority(#book.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<String> createBook(EBook book) {
        return delegate.createBook(book);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#libraryId + ':" + ROLE_LIBRARIAN + "', " +
            "#libraryId + ':" + ROLE_STUDENT + "')")
    public Flux<Book> getBooks(String libraryId, int pageNo) {
        return delegate.getBooks(libraryId, pageNo);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#libraryId + ':" + ROLE_LIBRARIAN + "', " +
            "#libraryId + ':" + ROLE_STUDENT + "')")
    public Flux<Book> getBooksByTitleAndAuthor(String libraryId, String titlePrefix, String authorPrefix, int pageNo) {
        return delegate.getBooksByTitleAndAuthor(libraryId, titlePrefix, authorPrefix, pageNo);
    }

    @Override
    public Mono<Book> getBook(String bookId) {
        return delegate.getBook(bookId)
                .flatMap(book -> Mono.fromSupplier(book::getLibraryId)
                        .map(libraryId -> List.of(libraryId + ":" + ROLE_LIBRARIAN, libraryId + ":" + ROLE_STUDENT))
                        .flatMap(SecurityUtil::hasAnyAuthority)
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                        .then(Mono.just(book)));
    }

    @Override
    @PreAuthorize("hasAuthority(#book.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<Void> updateBook(PhysicalBook book) {
        return delegate.updateBook(book);
    }

    @Override
    @PreAuthorize("hasAuthority(#book.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<Void> updateBook(EBook book) {
        return delegate.updateBook(book);
    }

    @Override
    public Mono<Void> deleteBook(String bookId) {
        return delegate
                .getBook(bookId)
                .map(Book::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .flatMap(SecurityUtil::hasAuthority)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .flatMap(alwaysTrue -> delegate.deleteBook(bookId));
    }

    @Override
    public Mono<Void> onLeaseAccept(AcceptedLease request) {
        return delegate.onLeaseAccept(request);
    }

    @Override
    public Mono<Void> onLeaseRelinquish(LeaseRequest leaseRequest) {
        return delegate.onLeaseRelinquish(leaseRequest);
    }
}
