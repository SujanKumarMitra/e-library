package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@AllArgsConstructor
public class JacksonValidUpdatePhysicalBookRequestAdaptor extends PhysicalBook {
    @NonNull
    @Delegate
    private final JacksonValidUpdatePhysicalBookRequest request;
}
