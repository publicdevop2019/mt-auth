package com.mt.integration.single.access.user.validation;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.ForgetPasswordRequest;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("validation")
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class UserForgetPwdValidationTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void validation_forget_pwd_email() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        User user = UserUtility.randomEmailPwdUser();
        UserUtility.emailPwdLogin(user);
        ForgetPasswordRequest originalReq = new ForgetPasswordRequest();
        //null
        originalReq.setEmail(null);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        originalReq.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        originalReq.setEmail("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //max length
        originalReq.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //invalid format
        originalReq.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_forget_pwd_country_code() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        User user = UserUtility.randomMobilePwdUser();
        UserUtility.mobilePwdLogin(user);
        ForgetPasswordRequest originalReq = new ForgetPasswordRequest();
        originalReq.setCountryCode(user.getCountryCode());
        originalReq.setMobileNumber(user.getMobileNumber());
        //both null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber("1231231234");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //blank
        originalReq.setCountryCode(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        originalReq.setCountryCode("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //invalid value
        originalReq.setCountryCode("7788");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_forget_pwd_mobile_number() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        User user = UserUtility.randomMobilePwdUser();
        UserUtility.mobilePwdLogin(user);
        ForgetPasswordRequest originalReq = new ForgetPasswordRequest();
        originalReq.setCountryCode(user.getCountryCode());
        originalReq.setMobileNumber(user.getMobileNumber());
        //both null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of null
        originalReq.setCountryCode("1");
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //blank
        originalReq.setMobileNumber(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        originalReq.setMobileNumber("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        originalReq.setMobileNumber("123");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //max length
        originalReq.setMobileNumber("1231231234123123");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid value
        originalReq.setMobileNumber("abcabcabcd");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_email() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        User user = UserUtility.randomEmailPwdUser();
        UserUtility.emailPwdLogin(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(token);

        //null
        forgetPasswordRequest.setEmail(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setEmail("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_code() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        User user = UserUtility.randomEmailPwdUser();
        UserUtility.emailPwdLogin(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(RandomUtility.randomPassword());
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(token);

        //null
        forgetPasswordRequest.setToken(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setToken(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setToken("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        forgetPasswordRequest.setToken("1");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        forgetPasswordRequest.setToken("01234567890123456789");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("987654321");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("abcdefghij");
        HttpEntity<ForgetPasswordRequest> request8 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request8, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_password() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        User user = UserUtility.randomEmailPwdUser();
        UserUtility.emailPwdLogin(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(RandomUtility.randomPassword());
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(token);

        //null
        forgetPasswordRequest.setNewPassword(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setNewPassword(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setNewPassword("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //min length
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        HttpEntity<ForgetPasswordRequest> request9 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange9 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request9, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange9.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        HttpEntity<ForgetPasswordRequest> request10 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange10 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request10, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange10.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_country_code() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        User user = UserUtility.randomMobilePwdUser();
        UserUtility.mobilePwdLogin(user);
        ForgetPasswordRequest originalReq = new ForgetPasswordRequest();
        originalReq.setCountryCode(user.getCountryCode());
        originalReq.setMobileNumber(user.getMobileNumber());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(originalReq, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        originalReq.setToken("123456789");
        originalReq.setNewPassword(RandomUtility.randomPassword());
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(token);

        //both null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(originalReq, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //one of null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber("1231231234");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //blank
        originalReq.setCountryCode(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange11 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange11.getStatusCode());
        //empty
        originalReq.setCountryCode("");
        HttpEntity<ForgetPasswordRequest> request12 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request12, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //invalid value
        originalReq.setCountryCode("7788");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_mobile_number() {
        String token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET).getBody()
            .getValue();
        User user = UserUtility.randomMobilePwdUser();
        UserUtility.mobilePwdLogin(user);
        ForgetPasswordRequest originalReq = new ForgetPasswordRequest();
        originalReq.setCountryCode(user.getCountryCode());
        originalReq.setMobileNumber(user.getMobileNumber());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(originalReq, headers);
        String url = HttpUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = HttpUtility.getAccessUrl("/users" + "/resetPwd");
        originalReq.setToken("123456789");
        originalReq.setNewPassword(RandomUtility.randomPassword());
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(token);

        //both null
        originalReq.setCountryCode(null);
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //one of null
        originalReq.setCountryCode("1");
        originalReq.setMobileNumber(null);
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //blank
        originalReq.setMobileNumber(" ");
        HttpEntity<ForgetPasswordRequest> request11 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange11 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request11, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange11.getStatusCode());
        //empty
        originalReq.setMobileNumber("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        originalReq.setMobileNumber("123");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //max length
        originalReq.setMobileNumber("1231231234123123");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid value
        originalReq.setMobileNumber("abcabcabcd");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(originalReq, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }
}