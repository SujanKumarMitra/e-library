package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
// TODO write tests
@Slf4j
class R2dbcPostgresqlEBookSegmentDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    @Test
    void givenValidEbookId_whenGetSegments_shouldGetSegments() {
    }

    @Test
    void givenNonExistingEbookId_whenGetSegments_shouldEmitEmpty() {
    }

    @Test
    void givenMalformedEbookId_whenGetSegments_shouldEmitEmpty() {
    }

    @Test
    void givenValidEbookIdAndIndex_whenGetSegment_shouldGetSegment() {
    }

    @Test
    void givenNonExistingEbookIdAndIndex_whenGetSegment_shouldEmitEmpty() {
    }

    @Test
    void givenMalformedEbookIdAndIndex_whenGetSegment_shouldEmitEmpty() {
    }

    @Test
    void givenValidEbookSegment_whenCreateSegment_shouldCreateSegment() {
    }

    @Test
    void givenNonExistingEbookId_whenCreateSegment_shouldEmitError() {
    }

    @Test
    void givenMalformedEbookId_whenCreateSegment_shouldEmitError() {
    }

    @Test
    void givenValidEbookId_whenDeleteAllSegments_shouldDeleteSegment() {
    }

    @Test
    void givenNonExistingEbookId_whenDeleteSegments_shouldEmitEmpty() {
    }

    @Test
    void givenMalformedEbookId_whenDeleteSegments_shouldEmitEmpty() {
    }

}