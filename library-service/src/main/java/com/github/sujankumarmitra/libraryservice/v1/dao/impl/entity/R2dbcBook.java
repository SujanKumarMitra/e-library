package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
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
    private Set<R2dbcTag> tags = new HashSet<>();

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
        this.coverPageImageId = book.getCoverPageImageId();

        if (book.getAuthors() != null) {
            for (Author author : book.getAuthors()) {
                this.authors.add(new R2dbcAuthor(author));
            }

        }
        if (book.getTags() != null) {
            for (Tag tag : book.getTags()) {
                this.tags.add(new R2dbcTag(tag));
            }
        }
    }

}
