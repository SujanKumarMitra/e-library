package com.github.sujankumarmitra.libraryservice.v1.util;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
public class UuidUtil {

    private UuidUtil() {
    }

    public static UUID nullableUuid(String uuidRepresentation) throws IllegalArgumentException {
        return uuidRepresentation == null ? null : fromString(uuidRepresentation);
    }

}
