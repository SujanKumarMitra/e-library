package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookSegmentService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultEBookSegmentService implements EBookSegmentService {

    @NonNull
    private final EBookSegmentDao ebookSegmentDao;
    @NonNull
    private final PagingProperties pagingProperties;

    @Override
    public Flux<EBookSegment> getSegmentsByEBookId(@NonNull String ebookId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return ebookSegmentDao.getSegmentsByBookId(ebookId, skip, pageSize);
    }

    @Override
    public Mono<EBookSegment> getSegmentByBookIdAndIndex(@NonNull String ebookId, int index) {
        return ebookSegmentDao.getSegmentByBookIdAndIndex(ebookId, index);
    }

    @Override
    public Mono<String> createSegment(@NonNull EBookSegment ebookSegment) {
        return ebookSegmentDao.createSegment(ebookSegment);
    }

    @Override
    public Mono<Void> deleteSegmentsByBookId(@NonNull String ebookId) {
        return ebookSegmentDao.deleteSegmentsByBookId(ebookId);
    }
}
