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
    private UUID id;
    private UUID bookId;
    private String name;


    public R2dbcAuthor() {
    }

    public R2dbcAuthor(@NonNull Author author) {
        this.id = author.getId() == null ? null : UUID.fromString(author.getId());
        this.bookId = author.getBookId() == null ? null : UUID.fromString(author.getBookId());
        this.name = author.getName();
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
}
