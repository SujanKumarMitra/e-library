package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
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
public final class R2dbcBookAuthor extends BookAuthor {
    private UUID id;
    private UUID bookId;
    private String name;

    public R2dbcBookAuthor() {
    }

    public R2dbcBookAuthor(@NonNull BookAuthor bookAuthor) {
        this.id = bookAuthor.getId() == null ? null : UUID.fromString(bookAuthor.getId());
        this.bookId = bookAuthor.getBookId() == null ? null : UUID.fromString(bookAuthor.getBookId());
        this.name = bookAuthor.getName();
    }

    public String getId() {
        return id == null ? null : id.toString();
    }

    public UUID getUuid() {
        return id;
    }

    @Override
    public String getBookId() {
        return bookId == null ? null : bookId.toString();
    }

    public UUID getBookUuid() {
        return bookId;
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
