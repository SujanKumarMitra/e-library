package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookSegmentService;
import com.github.sujankumarmitra.libraryservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityRoles.*;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredEBookSegmentService implements EBookSegmentService {

    @NonNull
    private final EBookSegmentService segmentService;
    @NonNull
    private final EBookDao ebookDao;

    @Override
    public Flux<EBookSegment> getSegmentsByEBookId(String ebookId, int pageNo) {
        return userHasAuthorities(ebookId, ROLE_STUDENT, ROLE_TEACHER, ROLE_LIBRARIAN)
                .thenMany(segmentService.getSegmentsByEBookId(ebookId, pageNo));
    }

    @Override
    public Mono<EBookSegment> getSegmentByBookIdAndIndex(String ebookId, int index) {
        return userHasAuthorities(ebookId, ROLE_STUDENT, ROLE_TEACHER, ROLE_LIBRARIAN)
                .then(segmentService.getSegmentByBookIdAndIndex(ebookId, index));
    }

    @Override
    public Mono<String> createSegment(EBookSegment ebookSegment) {
        return userHasAuthorities(ebookSegment.getBookId(), ROLE_LIBRARIAN)
                .then(segmentService.createSegment(ebookSegment));
    }

    @Override
    public Mono<Void> deleteSegmentsByBookId(String ebookId) {
        return userHasAuthorities(ebookId, ROLE_LIBRARIAN)
                .then(segmentService.deleteSegmentsByBookId(ebookId));
    }

    private Mono<Void> userHasAuthorities(String ebookId, String... roles) {
        return ebookDao.getBook(ebookId)
                .map(Book::getLibraryId)
                .flatMapMany(libraryId -> Flux.just(roles)
                        .map(role -> libraryId + ":" + role))
                .filterWhen(SecurityUtil::hasAuthority)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then();
    }

}
