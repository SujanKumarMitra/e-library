package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
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
public final class R2dbcEBook extends EBook {

    private UUID id;
    private String libraryId;
    private String title;
    private String publisher;
    private String edition;
    private String coverPageImageAssetId;
    private EBookFormat format;
    private Set<R2dbcBookAuthor> authors = new HashSet<>();
    private Set<R2dbcBookTag> tags = new HashSet<>();

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    public UUID getUuid() {
        return id;
    }

    public R2dbcEBook() {
    }

    public R2dbcEBook(@NonNull EBook physicalBook) {
        this.id = physicalBook.getId() == null ? null : UUID.fromString(physicalBook.getId());
        this.title = physicalBook.getTitle();
        this.publisher = physicalBook.getPublisher();
        this.edition = physicalBook.getEdition();
        this.coverPageImageAssetId = physicalBook.getCoverPageImageAssetId();
        this.format = physicalBook.getFormat();

        if (physicalBook.getAuthors() != null) {
            addAllAuthors(physicalBook.getAuthors());

        }
        if (physicalBook.getTags() != null) {
            addAllTags(physicalBook.getTags());
        }

    }

    public <T extends BookAuthor> void addAllAuthors(@NonNull Set<T> authors) {
        for (BookAuthor bookAuthor : authors) {
            this.authors.add(convertToR2dbcAuthor(bookAuthor));
        }
    }

    public <T extends BookTag> void addAllTags(@NonNull Set<T> tags) {
        for (BookTag author : tags) {
            this.tags.add(convertToR2dbcBookTag(author));
        }
    }

    private R2dbcBookAuthor convertToR2dbcAuthor(BookAuthor bookAuthor) {
        return bookAuthor instanceof R2dbcBookAuthor ?
                (R2dbcBookAuthor) bookAuthor :
                new R2dbcBookAuthor(bookAuthor);
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
