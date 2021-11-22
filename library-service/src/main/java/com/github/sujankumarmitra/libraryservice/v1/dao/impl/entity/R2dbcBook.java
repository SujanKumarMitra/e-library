package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
import lombok.Getter;
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

    public R2dbcBook(Book book) {

        this.title = book.getTitle();
        this.publisher = book.getPublisher();
        this.edition = book.getEdition();

        String id = book.getId();
        this.id = id == null ? null : UUID.fromString(id);

        Set<? extends Author> authors = book.getAuthors();
        if (authors != null) {
            for (Author author : authors)
                this.authors.add(new R2dbcAuthor(author));
        }
        Set<? extends Tag> tags = book.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                this.tags.add(new R2dbcTag(tag));
            }
        }
    }

}
