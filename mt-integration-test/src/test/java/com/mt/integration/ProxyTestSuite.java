package com.mt.integration;

import com.mt.integration.single.proxy.CorsTest;
import com.mt.integration.single.proxy.CsrfTest;
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
    CsrfTest.class,
    EndpointSecurityTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class,
    ProxyInfoTest.class,
    RevokeTokenTest.class,
})
public class ProxyTestSuite {
}
