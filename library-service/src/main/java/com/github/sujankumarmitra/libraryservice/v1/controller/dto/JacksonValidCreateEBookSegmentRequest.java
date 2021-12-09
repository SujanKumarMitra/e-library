package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidCreateEBookSegmentRequest extends EBookSegment {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String bookId;
    @NonNull
    @PositiveOrZero
    private Integer index;
    @NotEmpty
    private String assetId;
}
