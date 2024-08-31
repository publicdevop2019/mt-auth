package com.mt.integration.single.access.user;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.ForgetPasswordRequest;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserUpdatePwd;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class UserLoginTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void send_code_to_new_email() {
        User user = UserUtility.randomEmailOnlyUser();
        ResponseEntity<Void> pendingUser = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
    }

    @Test
    public void send_code_to_new_mobile() {
        User user = UserUtility.randomMobileOnlyUser();
        ResponseEntity<Void> pendingUser = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
    }

    @Test
    public void register_new_user_w_email_pwd() {
        User user = UserUtility.randomEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void login_new_user_w_email_pwd() {
        User user = UserUtility.randomEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void register_new_user_w_username_pwd() {
        User user = UserUtility.randomUsernamePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void login_new_user_w_username_pwd() {
        User user = UserUtility.randomUsernamePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void register_new_user_w_mobile_pwd() {
        User user = UserUtility.randomMobilePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void login_new_user_w_mobile_pwd() {
        User user = UserUtility.randomMobilePwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }


    @Test
    public void register_new_user_w_email_code() {
        User user = UserUtility.randomEmailOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void login_new_user_w_email_code() {
        User user = UserUtility.randomEmailOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void register_new_user_w_mobile_code() {
        User user = UserUtility.randomMobileOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void login_new_user_w_mobile_code() {
        User user = UserUtility.randomMobileOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void user_forget_password_email_w_pwd() {
        User user = UserUtility.randomEmailPwdUser();
        UserUtility.emailPwdLogin(user);
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmail(user.getEmail());
        user_forget_password_common(request);
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .emailPwdLogin(request.getEmail(),
                request.getNewPassword());
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void user_forget_password_email_w_code() {
        User user = UserUtility.randomEmailOnlyUser();
        UserUtility.emailCodeLogin(user);
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmail(user.getEmail());
        user_forget_password_common(request);
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .emailPwdLogin(request.getEmail(),
                request.getNewPassword());
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void user_forget_password_mobile_w_pwd() {
        User user = UserUtility.randomMobilePwdUser();
        UserUtility.mobilePwdLogin(user);
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setCountryCode(user.getCountryCode());
        request.setMobileNumber(user.getMobileNumber());
        user_forget_password_common(request);
        user.setPassword(request.getNewPassword());
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void user_forget_password_mobile_w_code() {
        User user = UserUtility.randomMobileOnlyUser();
        UserUtility.mobileCodeLogin(user);
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setCountryCode(user.getCountryCode());
        request.setMobileNumber(user.getMobileNumber());
        user_forget_password_common(request);
        user.setPassword(request.getNewPassword());
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    private void user_forget_password_common(ForgetPasswordRequest forgetPasswordRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String value = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        headers.setBearerAuth(value);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

    }
}