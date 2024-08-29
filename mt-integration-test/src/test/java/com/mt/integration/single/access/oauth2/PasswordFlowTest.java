package com.mt.integration.single.access.oauth2;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class PasswordFlowTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void get_access_token_and_refresh_token_for_clients_with_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assertions.assertNotNull(tokenResponse.getBody().getValue());
        Assertions.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
    }

    @Test
    public void get_access_token_only_for_clients_without_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getTokenWithUserEmailPwd(AppConstant.GRANT_TYPE_PASSWORD,
                AppConstant.CLIENT_ID_TEST_ID, AppConstant.COMMON_CLIENT_SECRET,
                AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assertions.assertNotNull(tokenResponse.getBody().getValue());
        Assertions.assertNull(tokenResponse.getBody().getRefreshToken());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_wrong_even_client_is_valid() {
        User user = UserUtility.randomUsernamePwdUser();
        ResponseEntity<?> tokenResponse = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        user.setPassword(RandomUtility.randomPassword());
        ResponseEntity<?> tokenResponse2 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse2.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_but_client_is_wrong() {
        ResponseEntity<?> tokenResponse = OAuth2Utility.getTokenWithUserEmailPwd(
            AppConstant.GRANT_TYPE_PASSWORD,
            "0C000001",
            AppConstant.COMMON_CLIENT_SECRET, AppConstant.ACCOUNT_EMAIL_ADMIN,
            AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_get_token_when_user_credentials_are_valid_and_client_is_valid_but_grant_type_is_wrong() {
        ResponseEntity<?> tokenResponse =
            OAuth2Utility.getTokenWithUserEmailPwd(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET,
                AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assertions.assertEquals(HttpStatus.OK,tokenResponse.getStatusCode());
    }


}
