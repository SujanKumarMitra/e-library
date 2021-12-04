package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidUpdateAuthorRequest extends Author {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String bookId;
    @Size(min = 1)
    private String name;

}
