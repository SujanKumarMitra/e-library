package com.github.sujankumarmitra.libraryservice.v1.util;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
public final class StringUtil {

    private StringUtil() {
    }

    public static <T> String nullableToString(T obj) {
        return obj == null ? null : obj.toString();
    }
}
