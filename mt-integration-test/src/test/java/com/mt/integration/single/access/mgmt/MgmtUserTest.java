package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserMgmt;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})

@Slf4j
public class MgmtUserTest {
    private static final String root_index = "0U8AZTODP4H0";

    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void admin_can_view_all_users() {
        String url = HttpUtility.getAccessUrl(AppConstant.USER_MGMT);
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtAdmin);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());
    }

    @Test
    public void admin_can_view_user_detail_include_login_history() {
        //include login history
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtAdmin);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.USER_MGMT), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        List<User> data = Objects.requireNonNull(exchange.getBody()).getData();
        int i = RandomUtility.pickRandomFromList(data.size());
        User user = data.get(i);
        ResponseEntity<UserMgmt> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(
                    HttpUtility.combinePath(AppConstant.USER_MGMT, user.getId())),
                HttpMethod.GET, request, UserMgmt.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assertions.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getLoginHistory());
    }

    @Test
    public void admin_can_lock_then_unlock_user() {
        User user = UserUtility.randomUsernamePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.usernamePwdLogin(user);


        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = Objects.requireNonNull(tokenResponse.getBody()).getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setLocked(true);
        user.setVersion(0);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        String url =
            HttpUtility.getAccessUrl(AppConstant.USER_MGMT + "/" + HttpUtility.getId(response));
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //login to verify account has been locked
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse1.getStatusCode());

        user.setLocked(false);
        user.setVersion(1);
        HttpEntity<User> request22 = new HttpEntity<>(user, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange22 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request22, DefaultOAuth2AccessToken.class);

        Assertions.assertEquals(HttpStatus.OK, exchange22.getStatusCode());
        //login to verify account has been unlocked
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());
    }


    @Test
    public void user_cannot_update_user_via_mgmt() {
        User user = UserUtility.randomEmailPwdUser();

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_USERNAME_USER, AppConstant.ACCOUNT_PASSWORD_USER);
        String bearer = Objects.requireNonNull(tokenResponse.getBody()).getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        String url =
            HttpUtility.getAccessUrl(AppConstant.USER_MGMT + "/" + root_index);
        ResponseEntity<Void> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request, Void.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void validation_mgmt_lock_user() {
        User user = UserUtility.randomUsernamePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.usernamePwdLogin(user);

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = Objects.requireNonNull(tokenResponse.getBody()).getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setLocked(null);
        user.setVersion(0);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        String url =
            HttpUtility.getAccessUrl(AppConstant.USER_MGMT + "/" + HttpUtility.getId(response));
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }
}
