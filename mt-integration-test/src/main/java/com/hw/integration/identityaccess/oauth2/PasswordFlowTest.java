package com.hw.integration.identityaccess.oauth2;

import static com.hw.helper.AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.hw.helper.AppConstant.GRANT_TYPE_PASSWORD;

import com.hw.helper.AppConstant;
import com.hw.helper.User;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UserUtility;
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
public class PasswordFlowTest {
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
    public void create_user_then_login() {
        User user = UserUtility.createUser();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = UserUtility.register(user);
        Assert.assertEquals(HttpStatus.OK, user1.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            user.getEmail(), user.getPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
    }

    @Test
    public void get_access_token_and_refresh_token_for_clients_with_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
    }

    @Test
    public void get_access_token_only_for_clients_without_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithUser(GRANT_TYPE_PASSWORD,
                AppConstant.CLIENT_ID_TEST_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNull(tokenResponse.getBody().getRefreshToken());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_wrong_even_client_is_valid() {
        ResponseEntity<?> tokenResponse = UserUtility.login("root2@gmail.com",
            AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_but_client_is_wrong() {
        ResponseEntity<?> tokenResponse = OAuth2Utility.getOAuth2WithUser(GRANT_TYPE_PASSWORD,
            "root2",
            AppConstant.EMPTY_CLIENT_SECRET, AppConstant.ACCOUNT_USERNAME_ADMIN,
            AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_and_client_is_valid_but_grant_type_is_wrong() {
        ResponseEntity<?> tokenResponse =
            OAuth2Utility.getOAuth2WithUser(GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }


}
