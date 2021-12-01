package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.List;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Ebook extends Book {

    public abstract <T extends EBookSegment> List<T> getSegments();

}
