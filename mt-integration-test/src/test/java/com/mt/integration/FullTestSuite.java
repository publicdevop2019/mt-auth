package com.mt.integration;

import com.mt.integration.concurrent.GatewayFilterConcurrentTest;
import com.mt.integration.single.access.oauth2.AuthorizationCodeTest;
import com.mt.integration.single.access.oauth2.ClientCredentialsTest;
import com.mt.integration.concurrent.ClientIdempotentTest;
import com.mt.integration.single.access.oauth2.PasswordFlowTest;
import com.mt.integration.single.access.oauth2.RefreshTokenTest;
import com.mt.integration.single.access.user.UserLoginTest;
import com.mt.integration.single.access.mgmt.MgmtClientTest;
import com.mt.integration.single.access.mgmt.MgmtEndpointTest;
import com.mt.integration.single.access.mgmt.MgmtProjectTest;
import com.mt.integration.single.access.mgmt.MgmtTest;
import com.mt.integration.single.access.mgmt.MgmtUserTest;
import com.mt.integration.single.access.mgmt.MgmtUtilityTest;
import com.mt.integration.single.access.tenant.SubscriptionTest;
import com.mt.integration.single.access.tenant.VersionTest;
import com.mt.integration.single.proxy.CorsTest;
import com.mt.integration.single.proxy.EndpointSecurityTest;
import com.mt.integration.single.proxy.GatewayFilterTest;
import com.mt.integration.single.proxy.JwtSecurityTest;
import com.mt.integration.single.proxy.RevokeTokenTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
@Suite
@SelectClasses({
    TenantTestSuite.class,
    ValidationTestSuite.class,
    MgmtClientTest.class,
    MgmtEndpointTest.class,
    MgmtProjectTest.class,
    MgmtTest.class,
    MgmtUserTest.class,
    MgmtUtilityTest.class,
    SubscriptionTest.class,
    VersionTest.class,
    AuthorizationCodeTest.class,
    ClientCredentialsTest.class,
    ClientIdempotentTest.class,
    PasswordFlowTest.class,
    RefreshTokenTest.class,
    UserLoginTest.class,
    CorsTest.class,
    EndpointSecurityTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class,
    RevokeTokenTest.class,
    GatewayFilterConcurrentTest.class
})
public class FullTestSuite {
}
