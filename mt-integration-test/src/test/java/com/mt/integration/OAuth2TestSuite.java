package com.mt.integration;

import com.mt.integration.single.access.oauth2.AuthorizationCodeTest;
import com.mt.integration.single.access.oauth2.ClientCredentialsTest;
import com.mt.integration.single.access.oauth2.PasswordFlowTest;
import com.mt.integration.single.access.oauth2.RefreshTokenTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AuthorizationCodeTest.class,
    ClientCredentialsTest.class,
    PasswordFlowTest.class,
    RefreshTokenTest.class,
})
public class OAuth2TestSuite {
}
