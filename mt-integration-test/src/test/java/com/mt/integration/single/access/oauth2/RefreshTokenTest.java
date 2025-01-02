package com.mt.integration.single.access.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class RefreshTokenTest {
    protected static TenantContext tenantContext;
    protected static Client client;

    @BeforeAll
    public static void beforeAll() {
        Project project = new Project();
        project.setId(AppConstant.TEST_PROJECT_ID);
        client = new Client();
        client.setId(AppConstant.CLIENT_ID_TEST_PASSWORD);
        client.setClientSecret(AppConstant.COMMON_CLIENT_SECRET);
        client.setPath("ecefaeca-svc");
        User user = new User();
        user.setEmail(AppConstant.ACCOUNT_USERNAME_TEST);
        user.setPassword(AppConstant.ACCOUNT_PASSWORD_TEST);
        tenantContext = new TenantContext();
        tenantContext.setProject(project);
        tenantContext.setCreator(user);
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void refresh_token_should_work() throws InterruptedException {
        //get jwt
        User user = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> token =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //access endpoint
        String url = HttpUtility.getTenantUrl(AppConstant.SVC_NAME_AUTH, "users/profile");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getBody().getValue());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Thread.sleep(60 * 1000 + 60 * 1000 + 2 * 1000);//spring cloud gateway add 60S leeway
        //access token should expire
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get access token with refresh token
        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getRefreshTokenResponse(token.getBody().getRefreshToken().getValue(),
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //use new access token for api call
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(exchange1.getBody().getValue());
        HttpEntity<String> request3 = new HttpEntity<>(null, headers3);
        ResponseEntity<String> exchange3 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request3, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void refresh_token_should_have_exp() {
        //get jwt
        User user = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> token =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //get jwt
        OAuth2RefreshToken refreshToken = token.getBody().getRefreshToken();
        String jwt = refreshToken.getValue();
        String jwtBody;
        try {
            jwtBody = jwt.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("malformed jwt token");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String s = new String(decode);
        Integer exp;
        try {
            Map<String, Object> var0 =
                TestContext.mapper.readValue(s, new TypeReference<>() {
                });
            exp = (Integer) var0.get("exp");
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "unable to find authorities in authorization header");
        }
        Assertions.assertNotNull(exp);
    }

    @Test
    public void refresh_token_will_exp() throws InterruptedException {
        //get jwt
        User user = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> token =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //access endpoint
        String url = HttpUtility.getTenantUrl(AppConstant.SVC_NAME_AUTH, "users/profile");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getBody().getValue());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //wait for refresh token expire
        Thread.sleep(180 * 1000 + 2 * 1000);
        //get access token with refresh token
        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getRefreshTokenResponse(token.getBody().getRefreshToken().getValue(),
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange1.getStatusCode());
    }

    @Test
    public void modified_refresh_token_should_not_work() throws InterruptedException {
        //get jwt
        User user = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> token =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //modify refresh token
        OAuth2RefreshToken refreshToken = token.getBody().getRefreshToken();
        String jwt = refreshToken.getValue();
        String jwtBody;
        String jwtHead;
        String jwtTail;
        String modifiedJwt;
        try {
            jwtHead = jwt.split("\\.")[0];
            jwtBody = jwt.split("\\.")[1];
            jwtTail = jwt.split("\\.")[2];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("malformed jwt token");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String payload = new String(decode);
        Integer exp;
        try {
            Map<String, Object> var0 =
                TestContext.mapper.readValue(payload, new TypeReference<>() {
                });
            exp = (Integer) var0.get("exp");
            var0.put("exp", Instant.now().getEpochSecond() + 10000);
            String s = TestContext.mapper.writeValueAsString(var0);
            Base64.Encoder encoder = Base64.getEncoder();
            jwtBody = new String(encoder.encode(s.getBytes()));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "unable to find authorities in authorization header");
        }
        modifiedJwt = String.join(".", jwtHead, jwtBody, jwtTail);
        //get new access token with refresh token
        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getRefreshTokenResponse(modifiedJwt,
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange1.getStatusCode());
    }
}


































