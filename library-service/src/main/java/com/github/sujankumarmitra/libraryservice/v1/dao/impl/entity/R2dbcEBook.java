package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.*;
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
    private String title;
    private String publisher;
    private String edition;
    private String coverPageImageAssetId;
    private EBookFormat format;
    private Set<R2dbcAuthor> authors = new HashSet<>();
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

}
