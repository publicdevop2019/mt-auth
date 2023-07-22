package com.mt.test_case.integration;

import com.mt.test_case.integration.single.access.validation.UserValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantCacheValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantClientValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantCorsValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantEndpointValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantMarketValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantPermissionValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantProjectValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantRoleValidationTest;
import com.mt.test_case.integration.single.access.validation.TenantUserValidationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserValidationTest.class,
    TenantCacheValidationTest.class,
    TenantClientValidationTest.class,
    TenantCorsValidationTest.class,
    TenantEndpointValidationTest.class,
    TenantMarketValidationTest.class,
    TenantPermissionValidationTest.class,
    TenantProjectValidationTest.class,
    TenantRoleValidationTest.class,
    TenantUserValidationTest.class,
})
public class ValidationTestSuite {
}
