package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreatePackageItemRequest {
    @NotEmpty
    private String bookId;
}
