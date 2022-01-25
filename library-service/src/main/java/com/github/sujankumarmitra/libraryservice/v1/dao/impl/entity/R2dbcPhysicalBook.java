package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
@Getter
@Setter
public final class R2dbcPhysicalBook extends PhysicalBook {

    private UUID id;
    private String libraryId;
    private String title;
    private Set<R2dbcAuthor> authors = new HashSet<>();
    private String publisher;
    private String edition;
    private String coverPageImageAssetId;
    private Set<R2dbcBookTag> tags = new HashSet<>();
    private R2dbcMoney finePerDay;
    private Long copiesAvailable;

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    public UUID getUuid() {
        return id;
    }

    public R2dbcPhysicalBook() {
    }

    public R2dbcPhysicalBook(@NonNull PhysicalBook physicalBook) {
        this.id = physicalBook.getId() == null ? null : UUID.fromString(physicalBook.getId());
        this.title = physicalBook.getTitle();
        this.publisher = physicalBook.getPublisher();
        this.edition = physicalBook.getEdition();
        this.coverPageImageAssetId = physicalBook.getCoverPageImageAssetId();

        if (physicalBook.getAuthors() != null) {
            addAllAuthors(physicalBook.getAuthors());

        }
        if (physicalBook.getTags() != null) {
            addAllTags(physicalBook.getTags());
        }
        this.finePerDay = physicalBook.getFinePerDay() == null ? null : new R2dbcMoney(physicalBook.getFinePerDay());
        this.copiesAvailable = physicalBook.getCopiesAvailable();
    }

    public <T extends Author> void addAllAuthors(@NonNull Set<T> authors) {
        for (Author author : authors) {
            this.authors.add(convertToR2dbcAuthor(author));
        }
    }

    public <T extends BookTag> void addAllTags(@NonNull Set<T> tags) {
        for (BookTag author : tags) {
            this.tags.add(convertToR2dbcBookTag(author));
        }
    }

    private R2dbcAuthor convertToR2dbcAuthor(Author author) {
        return author instanceof R2dbcAuthor ?
                (R2dbcAuthor) author :
                new R2dbcAuthor(author);
    }

    private R2dbcBookTag convertToR2dbcBookTag(BookTag tag) {
        return tag instanceof R2dbcBookTag ?
                (R2dbcBookTag) tag :
                new R2dbcBookTag(tag);
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
