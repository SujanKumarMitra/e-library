package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
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
public final class R2dbcBook extends Book {

    private UUID id;
    private String title;
    private Set<R2dbcAuthor> authors = new HashSet<>();
    private String publisher;
    private String edition;
    private String coverPageImageId;
    private String coverPageImageAssetId;
    private Set<R2dbcBookTag> tags = new HashSet<>();

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    public UUID getUuid() {
        return id;
    }

    public R2dbcBook() {
    }

    public R2dbcBook(@NonNull Book book) {

        this.id = book.getId() == null ? null : UUID.fromString(book.getId());
        this.title = book.getTitle();
        this.publisher = book.getPublisher();
        this.edition = book.getEdition();
        this.coverPageImageId = book.getCoverPageImageAssetId();

        if (book.getAuthors() != null) {
            addAllAuthors(book.getAuthors());

        }
        if (book.getTags() != null) {
            addAllTags(book.getTags());
        }
    }

//    public void addAuthor(@NonNull Author author) {
//        this.authors.add(convertToR2dbcAuthor(author));
//    }
//
//    public void addTag(@NonNull BookTag tag) {
//        this.tags.add(convertToR2dbcBookTag(tag));
//    }

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

//    public void removeAllTags() {
//        this.tags.clear();
//    }
//
//    public void removeAllAuthors() {
//        this.authors.clear();
//    }

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
