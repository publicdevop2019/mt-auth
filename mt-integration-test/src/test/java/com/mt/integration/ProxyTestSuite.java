package com.mt.integration;

import com.mt.integration.concurrent.ClientIdempotentTest;
import com.mt.integration.concurrent.GatewayFilterConcurrentTest;
import com.mt.integration.single.access.oauth2.AuthorizationCodeTest;
import com.mt.integration.single.access.oauth2.ClientCredentialsTest;
import com.mt.integration.single.access.oauth2.PasswordFlowTest;
import com.mt.integration.single.access.oauth2.RefreshTokenTest;
import com.mt.integration.single.proxy.CorsTest;
import com.mt.integration.single.proxy.EndpointSecurityTest;
import com.mt.integration.single.proxy.GatewayFilterTest;
import com.mt.integration.single.proxy.JwtSecurityTest;
import com.mt.integration.single.proxy.ProxyInfoTest;
import com.mt.integration.single.proxy.RevokeTokenTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CorsTest.class,
    EndpointSecurityTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class,
    RevokeTokenTest.class,
    ProxyInfoTest.class,
})
public class ProxyTestSuite {
}
