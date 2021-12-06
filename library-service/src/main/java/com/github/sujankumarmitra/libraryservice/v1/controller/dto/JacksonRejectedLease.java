package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@AllArgsConstructor
public class JacksonRejectedLease extends RejectedLease {
    @NonNull
    @Delegate
    private final RejectedLease rejectedLease;
}
