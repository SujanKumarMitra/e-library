package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@AllArgsConstructor
public class JacksonValidCreateEBookRequestAdaptor extends EBook {
    @NonNull
    @Delegate
    private final JacksonValidCreateEBookRequest request;
}