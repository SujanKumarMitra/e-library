package com.github.sujankumarmitra.assetservice.v1.service;

import org.junit.jupiter.api.Disabled;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@SelectClasses({
        LocalDiskBasedAssetStorageServiceTest.class,
        DefaultAssetPermissionServiceTest.class,
        DefaultAssetServiceTest.class,
        MongoWriteThenReadTest.class
})
@RunWith(JUnitPlatform.class)
@Disabled
class ServiceTests {
}
