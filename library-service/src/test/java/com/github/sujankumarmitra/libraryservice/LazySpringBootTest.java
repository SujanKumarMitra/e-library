package com.github.sujankumarmitra.libraryservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author skmitra
 * @since Jan 26/01/22, 2022
 */
@Target({TYPE})
@Retention(RUNTIME)
@SpringBootTest("spring.main.lazy-initialization=true")
@ActiveProfiles("test")
public @interface LazySpringBootTest {
}
