package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookPermission;
import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
@Setter
public class DefaultEBookPermission extends EBookPermission {
    private String bookId;
    private String userId;
    private Long startTime;
    private Long endTime;
}
