package com.hw.integration;

import com.hw.integration.identityaccess.proxy.CORSTest;
import com.hw.integration.identityaccess.oauth2.*;
import com.hw.integration.mall.CatalogTest;
import com.hw.integration.mall.ProductTest;
import com.hw.integration.profile.AddressTest;
import com.hw.integration.profile.CartTest;
import com.hw.integration.profile.OrderTest;
import com.hw.integration.identityaccess.proxy.*;
import com.hw.integration.identityaccess.proxy.JwtSecurityTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        AuthorizationCodeTest.class,
        BizClientTest.class,
        ClientCredentialsTest.class,
        PasswordFlowTest.class,
        BIzUserTest.class,
//        ProductServiceTest.class,
        CatalogTest.class,
        ProductTest.class,
        AddressTest.class,
        CartTest.class,
        OrderTest.class,
//        OrderServiceTest.class,
        RevokeTokenTest.class,
        BizClientApiSecurityTest.class,
        CORSTest.class,
        BizUserApiSecurityTest.class,
        EndpointTest.class,
        JwtSecurityTest.class

})
public class IntegrationTestSuite {
}
