package com.hw.integration;

import com.hw.integration.identityaccess.oauth2.AuthorizationCodeTest;
import com.hw.integration.identityaccess.oauth2.ClientCredentialsTest;
import com.hw.integration.identityaccess.oauth2.ClientTest;
import com.hw.integration.identityaccess.oauth2.PasswordFlowTest;
import com.hw.integration.identityaccess.oauth2.UserTest;
import com.hw.integration.identityaccess.proxy.CORSTest;
import com.hw.integration.identityaccess.proxy.ClientApiSecurityTest;
import com.hw.integration.identityaccess.proxy.EndpointTest;
import com.hw.integration.identityaccess.proxy.GatewayFilterTest;
import com.hw.integration.identityaccess.proxy.JwtSecurityTest;
import com.hw.integration.identityaccess.proxy.RevokeTokenTest;
import com.hw.integration.identityaccess.proxy.UserApiSecurityTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
    AuthorizationCodeTest.class,
    ClientTest.class,
    ClientCredentialsTest.class,
    PasswordFlowTest.class,
    UserTest.class,
    RevokeTokenTest.class,
    ClientApiSecurityTest.class,
    CORSTest.class,
    UserApiSecurityTest.class,
    EndpointTest.class,
    GatewayFilterTest.class,
    JwtSecurityTest.class

})
public class IntegrationTestSuite {
}
