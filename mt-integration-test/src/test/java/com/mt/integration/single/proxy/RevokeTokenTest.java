package com.mt.integration.single.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
public class RevokeTokenTest {
    private static final String PROXY_BLACKLIST = "/auth-svc/mgmt/revoke-tokens";

    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void receive_request_blacklist_client_then_block_client_old_request_which_trying_to_access_proxy_external_endpoints()
        throws JsonProcessingException, InterruptedException {
        String url = AppConstant.PROXY_URL + PROXY_BLACKLIST;
        String url2 = AppConstant.PROXY_URL + AppConstant.SVC_NAME_TEST + "/get/test";
        //before client get blacklisted, client is able to access auth server none token endpoint
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getPasswordFlowEmailPwdToken(
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET,
                AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer1 = tokenResponse1.getBody().getValue();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange1 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange1.getStatusCode());


        //block client
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", AppConstant.CLIENT_ID_LOGIN_ID);
        stringStringHashMap.put("type", "CLIENT");
        String s = TestContext.mapper.writeValueAsString(stringStringHashMap);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserUtility.getJwtAdmin());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        //after client get blacklisted, client with old token will get 401
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());

        //after client obtain new token from auth server, it can access resource again
        //add thread sleep to prevent token get revoked and generate within a second
        Thread.sleep(1000);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse3 =
            OAuth2Utility.getPasswordFlowEmailPwdToken(
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET,
                AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer3 = tokenResponse3.getBody().getValue();
        headers1.setBearerAuth(bearer3);
        HttpEntity<Object> hashMapHttpEntity3 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity3, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void only_root_user_can_add_blacklist_client() throws JsonProcessingException {

        String url = AppConstant.PROXY_URL + PROXY_BLACKLIST;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", AppConstant.CLIENT_ID_LOGIN_ID);
        stringStringHashMap.put("type", "CLIENT");
        String s = TestContext.mapper.writeValueAsString(stringStringHashMap);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserUtility.getJwtAdmin());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void receive_request_blacklist_user_then_block_user_old_request()
        throws JsonProcessingException, InterruptedException {
        //create new user to avoid impact other tests
        String jwtAdmin = UserUtility.getJwtAdmin();
        User user = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        String publicUrl = AppConstant.PROXY_URL + AppConstant.SVC_NAME_TEST + "/external/shared/no/auth";
        //user can log in & call test api & refresh token should work
        String bearer0 = pwdTokenResponse.getBody().getValue();
        String refreshToken = pwdTokenResponse.getBody().getRefreshToken().getValue();
        String userId = (String) pwdTokenResponse.getBody().getAdditionalInformation().get("uid");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer0);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(publicUrl, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> refreshTokenResponse =
            OAuth2Utility.getRefreshTokenResponse(refreshToken, AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.OK, refreshTokenResponse.getStatusCode());

        //blacklist user account
        String url = AppConstant.PROXY_URL + PROXY_BLACKLIST;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", userId);
        stringStringHashMap.put("type", "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtAdmin);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity =
            new HttpEntity<>(TestContext.mapper.writeValueAsString(stringStringHashMap), headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        //user request get blocked, even refresh token should not work
        ResponseEntity<String> exchange1 = TestContext.getRestTemplate()
            .exchange(publicUrl, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange1.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> refreshTokenResponse2 =
            OAuth2Utility.getRefreshTokenResponse(refreshToken, AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, refreshTokenResponse2.getStatusCode());
        //after resourceOwner obtain new token, access is permitted
        //add thread sleep to prevent token get revoked and generate within a second
        Thread.sleep(1000);
        ResponseEntity<DefaultOAuth2AccessToken> newToken =
            UserUtility.emailPwdLogin(user.getEmail(), user.getPassword());
        headers1.setBearerAuth(newToken.getBody().getValue());
        HttpEntity<Object> hashMapHttpEntity3 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 = TestContext.getRestTemplate()
            .exchange(publicUrl, HttpMethod.GET, hashMapHttpEntity3, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());

    }

    @Test
    public void validation_revoke_token_id() {
        //TODO
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
