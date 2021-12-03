package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultBookService implements BookService {

    @NotNull
    private final PhysicalBookDao physicalBookDao;
    @NotNull
    private final EBookDao eBookDao;

    @Override
    public Mono<String> createBook(PhysicalBook book) {
        return physicalBookDao.createBook(book);
    }

    @Override
    public Mono<String> createBook(EBook book) {
        return eBookDao.createBook(book);
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
}
