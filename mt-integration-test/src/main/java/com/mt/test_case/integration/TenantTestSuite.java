package com.mt.test_case.integration;

import com.mt.test_case.integration.single.access.tenant.TenantAdminTest;
import com.mt.test_case.integration.single.access.tenant.TenantCacheTest;
import com.mt.test_case.integration.single.access.tenant.TenantClientTest;
import com.mt.test_case.integration.single.access.tenant.TenantCorsTest;
import com.mt.test_case.integration.single.access.tenant.TenantEndpointTest;
import com.mt.test_case.integration.single.access.tenant.TenantMarketTest;
import com.mt.test_case.integration.single.access.tenant.TenantMessageTest;
import com.mt.test_case.integration.single.access.tenant.TenantPermissionTest;
import com.mt.test_case.integration.single.access.tenant.TenantProjectTest;
import com.mt.test_case.integration.single.access.tenant.TenantRoleTest;
import com.mt.test_case.integration.single.access.tenant.TenantUserTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
    TenantAdminTest.class,
    TenantCacheTest.class,
    TenantClientTest.class,
    TenantCorsTest.class,
    TenantEndpointTest.class,
    TenantMarketTest.class,
    TenantMessageTest.class,
    TenantPermissionTest.class,
    TenantProjectTest.class,
    TenantRoleTest.class,
    TenantUserTest.class,
})
public class TenantTestSuite {
}
