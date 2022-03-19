package com.hw.integration.identityaccess.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.hw.helper.UserAction.*;


@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class PasswordFlowTest {
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Autowired
    private UserAction action;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void create_user_then_login() {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = action.registerAnUser(user);
        Assert.assertEquals(HttpStatus.OK, user1.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
    }

    @Test
    public void get_access_token_and_refresh_token_for_clients_with_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword( ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNotNull(tokenResponse.getBody().getRefreshToken().getValue());
    }

    @Test
    public void get_access_token_only_for_clients_without_refresh_configured() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD,ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, CLIENT_ID_TEST_ID, EMPTY_CLIENT_SECRET);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
        Assert.assertNull(tokenResponse.getBody().getRefreshToken());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_wrong_even_client_is_valid() {
        ResponseEntity<?> tokenResponse = action.getJwtPassword("root2@gmail.com", ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_but_client_is_wrong() {
        ResponseEntity<?> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, "root2", EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());
    }

    @Test
    public void should_not_get_token_when_user_credentials_are_valid_and_client_is_valid_but_grant_type_is_wrong() {
        ResponseEntity<?> tokenResponse = getTokenResponse(GRANT_TYPE_CLIENT_CREDENTIALS, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String username, String userPwd, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return action.restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }


}
