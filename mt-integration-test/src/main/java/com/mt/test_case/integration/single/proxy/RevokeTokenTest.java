package com.mt.test_case.integration.single.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.utility.OAuth2Utility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.CommonTest;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this integration auth requires oauth2service to be running.
 */
@Slf4j
@RunWith(SpringRunner.class)
public class RevokeTokenTest  extends CommonTest {
    public static final String PROXY_BLACKLIST = "/auth-svc/mgmt/revoke-tokens";

    @Test
    public void receive_request_blacklist_client_then_block_client_old_request_which_trying_to_access_proxy_external_endpoints()
        throws JsonProcessingException, InterruptedException {
        Thread.sleep(10000);
        String url = AppConstant.PROXY_URL + PROXY_BLACKLIST;
        String url2 = AppConstant.PROXY_URL + AppConstant.SVC_NAME_TEST + "/get/test";
        /**
         * before client get blacklisted, client is able to access auth server non token endpoint
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer1 = tokenResponse1.getBody().getValue();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange1 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());


        /**
         * block client
         */

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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * after client get blacklisted, client with old token will get 401
         */
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());

        /**
         * after client obtain new token from auth server, it can access resource again
         * add thread sleep to prevent token get revoked and generate within a second
         */
        Thread.sleep(1000);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse3 =
            OAuth2Utility.getOAuth2PasswordToken(
                AppConstant.CLIENT_ID_LOGIN_ID, AppConstant.EMPTY_CLIENT_SECRET,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer3 = tokenResponse3.getBody().getValue();
        headers1.setBearerAuth(bearer3);
        HttpEntity<Object> hashMapHttpEntity3 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.GET, hashMapHttpEntity3, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void receive_request_blacklist_user_then_block_user_old_request()
        throws JsonProcessingException, InterruptedException {
        String url2 = AppConstant.PROXY_URL + AppConstant.SVC_NAME_TEST + "/get/test";
        /**
         * user can login & call resourceOwner api & refresh token should work
         */
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse = UserUtility.getJwtPasswordAdmin();

        String bearer0 = pwdTokenResponse.getBody().getValue();
        String refreshToken = pwdTokenResponse.getBody().getRefreshToken().getValue();
        String userId = (String) pwdTokenResponse.getBody().getAdditionalInformation().get("uid");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer0);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> refreshTokenResponse =
            OAuth2Utility.getRefreshTokenResponse(refreshToken, AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.OK, refreshTokenResponse.getStatusCode());

        /**
         * blacklist admin account
         */

        String url = AppConstant.PROXY_URL + PROXY_BLACKLIST;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("id", userId);
        stringStringHashMap.put("type", "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer0);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity =
            new HttpEntity<>(TestContext.mapper.writeValueAsString(stringStringHashMap), headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * resourceOwner request get blocked, even refresh token should not work
         */
        ResponseEntity<String> exchange1 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange1.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> refreshTokenResponse2 =
            OAuth2Utility.getRefreshTokenResponse(refreshToken, AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, refreshTokenResponse2.getStatusCode());

        /**
         * after resourceOwner obtain new token, access is permitted
         * add thread sleep to prevent token get revoked and generate within a second
         */
        Thread.sleep(1000);
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity3 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity3, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());

    }


}
