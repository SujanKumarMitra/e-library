package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public final class R2dbcEBookSegment extends EBookSegment {

    private UUID id;
    private UUID bookId;
    private Long index;
    private String assetId;

    public R2dbcEBookSegment() {
    }

    public R2dbcEBookSegment(@NonNull EBookSegment segment) {
        this.id = segment.getId() == null ? null : UUID.fromString(segment.getId());
        this.bookId = segment.getBookId() == null ? null : UUID.fromString(segment.getBookId());
        this.index = segment.getIndex();
        this.assetId = segment.getAssetId();
    }

    @Override
    public String getId() {
        return id == null ? null : id.toString();
    }

    @Override
    public String getBookId() {
        return bookId == null ? null : bookId.toString();
    }

}
