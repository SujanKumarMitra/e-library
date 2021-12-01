package com.github.sujankumarmitra.libraryservice.v1.openapi.schema;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */
@Getter
public enum BookTypeSchema {

    PHYSICAL_BOOK("physical_book"),

    EBOOK("ebook");

    public final String type;

    BookTypeSchema(String type) {
        this.type = type;
    }

    private static final Map<String, BookTypeSchema> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("physical_book", PHYSICAL_BOOK);
        TYPE_MAP.put("ebook", EBOOK);
    }

    public BookTypeSchema ofType(@NonNull String type) {
        BookTypeSchema bookType = TYPE_MAP.getOrDefault(type, null);
        if (bookType == null) throw new IllegalArgumentException();
        return bookType;
    }
}
