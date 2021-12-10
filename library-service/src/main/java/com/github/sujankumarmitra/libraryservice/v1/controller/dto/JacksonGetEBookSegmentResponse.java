package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@AllArgsConstructor
public class JacksonGetEBookSegmentResponse extends EBookSegment {
    @Delegate
    private final EBookSegment delegate;
}
