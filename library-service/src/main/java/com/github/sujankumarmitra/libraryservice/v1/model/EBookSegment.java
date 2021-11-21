package com.github.sujankumarmitra.libraryservice.v1.model;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class EBookSegment implements Comparable<EBookSegment> {

    public abstract String getId();

    public abstract String getBookId();

    public abstract long getStartPage();

    public abstract long getEndPage();

    @Override
    public int compareTo(EBookSegment o) {
        return Long.compare(getStartPage(), o.getStartPage());
    }
}
