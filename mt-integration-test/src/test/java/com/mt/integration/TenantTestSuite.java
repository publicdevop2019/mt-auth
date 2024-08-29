package com.mt.integration;

import com.mt.integration.single.access.tenant.SubscriptionTest;
import com.mt.integration.single.access.tenant.TenantAdminTest;
import com.mt.integration.single.access.tenant.TenantCacheTest;
import com.mt.integration.single.access.tenant.TenantClientTest;
import com.mt.integration.single.access.tenant.TenantCorsTest;
import com.mt.integration.single.access.tenant.TenantEndpointTest;
import com.mt.integration.single.access.tenant.TenantMarketTest;
import com.mt.integration.single.access.tenant.TenantMessageTest;
import com.mt.integration.single.access.tenant.TenantPermissionTest;
import com.mt.integration.single.access.tenant.TenantProjectTest;
import com.mt.integration.single.access.tenant.TenantRoleTest;
import com.mt.integration.single.access.tenant.TenantUserTest;
import com.mt.integration.single.access.tenant.VersionTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
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
    SubscriptionTest.class,
    VersionTest.class
})
public class TenantTestSuite {
}
