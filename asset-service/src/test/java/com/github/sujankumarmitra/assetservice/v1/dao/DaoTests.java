package com.github.sujankumarmitra.assetservice.v1.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RunWith(JUnitPlatform.class)
@SelectClasses({
        MongoAssetDaoTest.class,
        MongoAssetPermissionDao.class
})
class DaoTests {
}
