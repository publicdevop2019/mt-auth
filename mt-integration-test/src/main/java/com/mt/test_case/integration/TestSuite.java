package com.mt.test_case.integration;

import com.mt.test_case.integration.single.access.AuthorizationCodeTest;
import com.mt.test_case.integration.single.access.ClientCredentialsTest;
import com.mt.test_case.integration.single.access.ClientIdempotentTest;
import com.mt.test_case.integration.single.access.RefreshTokenTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtClientTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtEndpointTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtProjectTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtUserTest;
import com.mt.test_case.integration.single.access.mgmt.MgmtUtilityTest;
import com.mt.test_case.integration.single.access.tenant.TenantAdminTest;
import com.mt.test_case.integration.single.access.tenant.TenantCacheTest;
import com.mt.test_case.integration.single.access.tenant.TenantClientTest;
import com.mt.test_case.integration.single.access.PasswordFlowTest;
import com.mt.test_case.integration.single.access.UserTest;
import com.mt.test_case.integration.single.access.tenant.TenantCorsTest;
import com.mt.test_case.integration.single.access.tenant.TenantMarketTest;
import com.mt.test_case.integration.single.access.tenant.TenantMessageTest;
import com.mt.test_case.integration.single.access.tenant.TenantPermissionTest;
import com.mt.test_case.integration.single.access.tenant.TenantProjectTest;
import com.mt.test_case.integration.single.access.tenant.TenantRoleTest;
import com.mt.test_case.integration.single.access.tenant.TenantUserTest;
import com.mt.test_case.integration.single.proxy.CorsTest;
import com.mt.test_case.integration.single.proxy.EndpointSecurityTest;
import com.mt.test_case.integration.single.access.tenant.TenantEndpointTest;
import com.mt.test_case.integration.single.proxy.GatewayFilterTest;
import com.mt.test_case.integration.single.proxy.JwtSecurityTest;
import com.mt.test_case.integration.single.proxy.RevokeTokenTest;
import com.mt.test_case.integration.single.access.tenant.SubscriptionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
    MgmtClientTest.class,
    MgmtEndpointTest.class,
    MgmtProjectTest.class,
    MgmtTest.class,
    MgmtUserTest.class,
    MgmtUtilityTest.class,
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
    AuthorizationCodeTest.class,
    ClientCredentialsTest.class,
    ClientIdempotentTest.class,
    PasswordFlowTest.class,
    RefreshTokenTest.class,
    UserTest.class,
    CorsTest.class,
    EndpointSecurityTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class,
    RevokeTokenTest.class,
    SubscriptionTest.class,
})
public class TestSuite {
}
