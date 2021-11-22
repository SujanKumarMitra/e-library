package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
@Getter
@Setter
public final class R2dbcAuthor extends Author {
    private UUID bookId;
    private String name;

    public R2dbcAuthor() {
    }

    public R2dbcAuthor(@NonNull Author author) {
        String bookId = author.getBookId();
        this.bookId = bookId == null ? null : UUID.fromString(bookId);
        this.name = author.getName();
    }

    @Override
    public String getBookId() {
        return bookId == null ? null : bookId.toString();
    }
}
