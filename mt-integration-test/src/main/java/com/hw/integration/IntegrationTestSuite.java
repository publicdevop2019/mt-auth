package com.hw.integration;

import com.hw.integration.single.access.AuthorizationCodeTest;
import com.hw.integration.single.access.ClientCredentialsTest;
import com.hw.integration.single.access.TenantClientTest;
import com.hw.integration.single.access.PasswordFlowTest;
import com.hw.integration.single.access.UserTest;
import com.hw.integration.single.proxy.CORSTest;
import com.hw.integration.single.proxy.EndpointSecurityTest;
import com.hw.integration.single.access.EndpointTest;
import com.hw.integration.single.proxy.GatewayFilterTest;
import com.hw.integration.single.proxy.JwtSecurityTest;
import com.hw.integration.single.proxy.RevokeTokenTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
    AuthorizationCodeTest.class,
    TenantClientTest.class,
    ClientCredentialsTest.class,
    PasswordFlowTest.class,
    UserTest.class,
    RevokeTokenTest.class,
    EndpointSecurityTest.class,
    CORSTest.class,
    EndpointTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class

})
public class IntegrationTestSuite {
}
