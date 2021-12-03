package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonUpdateAuthorRequest {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String bookId;
    private String name;

}
