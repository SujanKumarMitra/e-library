package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidCreateAuthorRequest extends Author {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String bookId;
    @NotEmpty
    private String name;
}
