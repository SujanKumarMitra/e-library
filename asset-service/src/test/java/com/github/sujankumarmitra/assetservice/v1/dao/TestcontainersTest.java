package com.github.sujankumarmitra.assetservice.v1.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
class TestcontainersTest {

    @Test
    void testImageNameParsing() {
        DockerImageName parse = DockerImageName.parse("mongo");
        Assertions.assertThat(parse.getVersionPart()).isEqualTo("latest");
    }
}
