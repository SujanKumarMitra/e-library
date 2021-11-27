package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
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
public final class R2dbcBookTag extends BookTag {
    private UUID id;
    private UUID bookId;
    private String key;
    private String value;

    public R2dbcBookTag() {
    }

    public R2dbcBookTag(@NonNull BookTag tag) {
        this.id = tag.getId() == null ? null : UUID.fromString(tag.getId());
        this.bookId = tag.getBookId() == null ? null : UUID.fromString(tag.getBookId());
        this.key = tag.getKey();
        this.value = tag.getValue();
    }

    public UUID getUuidId() {
        return id;
    }

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    @Override
    public String getBookId() {
        return bookId == null ? null : bookId.toString();
    }

    public UUID getBookUuid() {
        return bookId;
    }

}
