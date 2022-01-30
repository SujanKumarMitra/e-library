package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultLibrarian extends Librarian {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String libraryId;
}
