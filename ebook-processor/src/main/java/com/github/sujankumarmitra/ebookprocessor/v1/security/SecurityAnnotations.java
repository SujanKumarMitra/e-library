package com.github.sujankumarmitra.ebookprocessor.v1.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
public final class SecurityAnnotations {

    @Target({METHOD})
    @Retention(RUNTIME)
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public @interface RoleStudent {
    }

    @Target({METHOD})
    @Retention(RUNTIME)
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public @interface RoleTeacher {
    }

    @Target({METHOD})
    @Retention(RUNTIME)
    @PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
    public @interface RoleLibrarian {
    }

    @Target({METHOD})
    @Retention(RUNTIME)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public @interface RoleAdmin {
    }
}
