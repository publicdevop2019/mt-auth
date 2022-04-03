package com.hw.integration.identityaccess.oauth2;

import static com.hw.helper.AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.hw.helper.AppConstant.GRANT_TYPE_PASSWORD;

import com.hw.helper.AppConstant;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class ClientCredentialsTest {
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, id {}", description.getMethodName(),
                TestContext.getTestId());
        }
    };

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    public void use_client_with_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(GRANT_TYPE_CLIENT_CREDENTIALS, AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
    }

    @Test
    public void use_client_with_empty_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(GRANT_TYPE_CLIENT_CREDENTIALS, AppConstant.CLIENT_ID_REGISTER_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assert.assertNotNull(tokenResponse.getBody().getValue());

    }

    @Test
    public void use_client_with_wrong_credentials() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(GRANT_TYPE_CLIENT_CREDENTIALS, AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void use_client_with_wrong_grant_type() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(GRANT_TYPE_PASSWORD, AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void trying_to_login_with_not_exist_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(GRANT_TYPE_PASSWORD, UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);

    }
}
