package com.mt.integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    MgmtTestSuite.class,
    MiscTestSuite.class,
    OAuth2TestSuite.class,
    ProxyTestSuite.class,
    TenantTestSuite.class,
    TenantValidationTestSuite.class,
    UserTestSuite.class,
    UserValidationTestSuite.class,
})
public class FullTestSuite {
}
