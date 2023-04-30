package com.hw.integration;

import com.hw.integration.single.access.AuthorizationCodeTest;
import com.hw.integration.single.access.ClientCredentialsTest;
import com.hw.integration.single.access.RefreshTokenTest;
import com.hw.integration.single.access.mgmt.MgmtClientTest;
import com.hw.integration.single.access.mgmt.MgmtEndpointTest;
import com.hw.integration.single.access.mgmt.MgmtProjectTest;
import com.hw.integration.single.access.mgmt.MgmtUserTest;
import com.hw.integration.single.access.tenant.TenantAdminTest;
import com.hw.integration.single.access.tenant.TenantCacheTest;
import com.hw.integration.single.access.tenant.TenantClientTest;
import com.hw.integration.single.access.PasswordFlowTest;
import com.hw.integration.single.access.UserTest;
import com.hw.integration.single.access.tenant.TenantCorsTest;
import com.hw.integration.single.access.tenant.TenantMarketTest;
import com.hw.integration.single.access.tenant.TenantMessageTest;
import com.hw.integration.single.access.tenant.TenantPermissionTest;
import com.hw.integration.single.access.tenant.TenantProjectTest;
import com.hw.integration.single.access.tenant.TenantRoleTest;
import com.hw.integration.single.access.tenant.TenantUserTest;
import com.hw.integration.single.proxy.CORSTest;
import com.hw.integration.single.proxy.EndpointSecurityTest;
import com.hw.integration.single.access.tenant.TenantEndpointTest;
import com.hw.integration.single.proxy.GatewayFilterTest;
import com.hw.integration.single.proxy.JwtSecurityTest;
import com.hw.integration.single.proxy.RevokeTokenTest;
import com.hw.integration.single.proxy.SubscriptionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
    AuthorizationCodeTest.class,
    ClientCredentialsTest.class,
    PasswordFlowTest.class,
    RefreshTokenTest.class,
    UserTest.class,
    CORSTest.class,
    EndpointSecurityTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class,
    RevokeTokenTest.class,
    SubscriptionTest.class,
    MgmtClientTest.class,
    MgmtEndpointTest.class,
    MgmtProjectTest.class,
    MgmtUserTest.class,
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
    TenantUserTest.class
})
public class IntegrationTestSuite {
}
