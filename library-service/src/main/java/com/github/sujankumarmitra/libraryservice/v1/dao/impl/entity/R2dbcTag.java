package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
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
public final class R2dbcTag extends Tag {
    private UUID bookId;
    private String key;
    private String value;

    public R2dbcTag() {
    }

    public R2dbcTag(@NonNull Tag tag) {
        String bookId = tag.getBookId();
        this.bookId = bookId == null ? null : UUID.fromString(bookId);
        this.key = tag.getKey();
        this.value = tag.getValue();
    }

    @Override
    public String getBookId() {
        return bookId == null ? null : bookId.toString();
    }

}
