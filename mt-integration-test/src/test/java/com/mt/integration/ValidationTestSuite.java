package com.mt.integration;

import com.mt.integration.single.access.tenant.validation.TenantCacheValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantClientValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantCorsValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantEndpointValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantMarketValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantPermissionValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantProjectValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantRoleValidationTest;
import com.mt.integration.single.access.tenant.validation.TenantUserValidationTest;
import com.mt.integration.single.access.user.validation.UserValidationTest;
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
