package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.StringUtil.nullableToString;
import static com.github.sujankumarmitra.libraryservice.v1.util.UuidUtil.nullableUuid;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
@Table("ebook_segments")
public final class R2dbcEBookSegment extends EBookSegment {

    @Id
    private UUID id;
    @Column("book_id")
    private UUID bookId;
    private Integer index;
    @Column("asset_id")
    private String assetId;

    public R2dbcEBookSegment() {
    }

    public R2dbcEBookSegment(@NonNull EBookSegment segment) {
        this.id = nullableUuid(segment.getId());
        this.bookId = nullableUuid(segment.getBookId());
        this.index = segment.getIndex();
        this.assetId = segment.getAssetId();
    }

    @Override
    public String getId() {
        return nullableToString(id);
    }

    public UUID getUuid() {
        return id;
    }

    @Override
    public String getBookId() {
        return nullableToString(bookId);
    }

    public UUID getBookUuid() {
        return bookId;
    }

}
