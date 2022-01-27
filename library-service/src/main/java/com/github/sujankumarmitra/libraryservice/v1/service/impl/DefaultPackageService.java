package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.LibraryIdMismatchException;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultPackageService implements PackageService {

    @NonNull
    private final PackageDao packageDao;
    @NonNull
    private final BookDao<Book> bookDao;
    @NonNull
    private final PagingProperties pagingProperties;

    @Override
    public Mono<String> createPackage(Package aPackage) {
        return handleIncorrectLibraryId(aPackage)
                .flatMap(packageDao::createPackage);
    }

    private Mono<Package> handleIncorrectLibraryId(Package aPackage) {
        return Flux.<PackageItem>fromIterable(aPackage.getItems())
                .map(PackageItem::getBookId)
                .flatMap(bookDao::getBook)
                .map(Book::getLibraryId)
                .filter(bookLibraryId -> !bookLibraryId.equals(aPackage.getLibraryId()))
                .flatMap(differentLibraryId -> Flux.<String>error(() ->
                        new LibraryIdMismatchException("given book id(s) libraryId is not equal to '" + differentLibraryId + "'")))
                .then(Mono.fromSupplier(() -> aPackage));
    }

    @Override
    public Flux<Package> getPackages(String libraryId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return packageDao
                .getPackages(libraryId, skip, pageSize);
    }

    @Override
    public Flux<Package> getPackagesByName(String libraryId, String namePrefix, int pageNo) {

        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return packageDao
                .getPackagesByNameStartingWith(libraryId, namePrefix, skip, pageSize);
    }

    @Override
    public Mono<Void> updatePackage(Package aPackage) {
        if (aPackage.getItems() != null) {
            return handleIncorrectLibraryId(aPackage)
                    .flatMap(packageDao::updatePackage);
        } else {
            return packageDao.updatePackage(aPackage);
        }
    }

    @Override
    public Mono<Void> deletePackage(String packageId) {
        return packageDao.deletePackage(packageId);
    }
}
