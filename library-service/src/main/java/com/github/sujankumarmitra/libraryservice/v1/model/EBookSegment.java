package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class EBookSegment {

    public abstract String getId();

    public abstract String getBookId();

    public abstract Integer getIndex();

    public abstract String getAssetId();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBookSegment)) return false;
        EBookSegment segment = (EBookSegment) o;
        return Objects.equals(getId(), segment.getId()) &&
                Objects.equals(getBookId(), segment.getBookId()) &&
                Objects.equals(getIndex(), segment.getIndex()) &&
                Objects.equals(getAssetId(), segment.getAssetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getBookId(),
                getIndex(),
                getAssetId());
    }

    @Override
    public String toString() {
        return "EBookSegment{" +
                "id='" + getId() + '\'' +
                ", bookId='" + getBookId() + '\'' +
                ", index=" + getIndex() +
                ", assetId='" + getAssetId() + '\'' +
                '}';
    }

}
