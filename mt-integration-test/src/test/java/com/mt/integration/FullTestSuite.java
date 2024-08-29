package com.mt.integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    TenantTestSuite.class,
    TenantValidationTestSuite.class,
    MgmtTestSuite.class,
    OAuth2TestSuite.class,
    UserTestSuite.class,
    ProxyTestSuite.class,
    MiscTestSuite.class,
})
public class FullTestSuite {
}
