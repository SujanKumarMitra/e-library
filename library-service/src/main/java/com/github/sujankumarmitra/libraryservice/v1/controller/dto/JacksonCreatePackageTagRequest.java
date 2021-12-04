package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreatePackageTagRequest {

    private String key;
    private String value;

}
