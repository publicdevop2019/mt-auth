package com.hw.integration;

import com.hw.integration.identityaccess.oauth2.*;
import com.hw.integration.identityaccess.proxy.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        AuthorizationCodeTest.class,
        BizClientTest.class,
        ClientCredentialsTest.class,
        PasswordFlowTest.class,
        BIzUserTest.class,
        RevokeTokenTest.class,
        BizClientApiSecurityTest.class,
        CORSTest.class,
        BizUserApiSecurityTest.class,
        EndpointTest.class,
        JwtSecurityTest.class

})
public class IntegrationTestSuite {
}
