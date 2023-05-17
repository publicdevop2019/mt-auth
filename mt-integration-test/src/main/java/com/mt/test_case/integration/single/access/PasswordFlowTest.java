package com.mt.test_case.integration.single.access;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.CommonTest;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.OAuth2Utility;
import com.mt.test_case.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@Slf4j
public class PasswordFlowTest  extends CommonTest {

    @Test
    public void create_user_then_login() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> user1 = UserUtility.register(user);
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
            OAuth2Utility.getOAuth2WithUser(AppConstant.GRANT_TYPE_PASSWORD,
                AppConstant.CLIENT_ID_TEST_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNull(tokenResponse.getBody().getRefreshToken());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_wrong_even_client_is_valid() {
        ResponseEntity<?> tokenResponse = UserUtility.login("root2@gmail.com",
            AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_but_client_is_wrong() {
        ResponseEntity<?> tokenResponse = OAuth2Utility.getOAuth2WithUser(
            AppConstant.GRANT_TYPE_PASSWORD,
            "root2",
            AppConstant.EMPTY_CLIENT_SECRET, AppConstant.ACCOUNT_USERNAME_ADMIN,
            AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_and_client_is_valid_but_grant_type_is_wrong() {
        ResponseEntity<?> tokenResponse =
            OAuth2Utility.getOAuth2WithUser(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }


}
