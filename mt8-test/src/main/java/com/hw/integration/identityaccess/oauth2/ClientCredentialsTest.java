package com.hw.integration.identityaccess.oauth2;

import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.UserAction;
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
import java.util.UUID;

import static com.hw.helper.UserAction.*;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class ClientCredentialsTest {
    @Autowired
    private UserAction action;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description,uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }
    @Test
    public void use_client_with_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_CLIENT_CREDENTIALS, CLIENT_ID_OAUTH2_ID, COMMON_CLIENT_SECRET);
        Assert.assertNotNull(tokenResponse.getBody().getValue());
    }

    @Test
    public void use_client_with_empty_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_CLIENT_CREDENTIALS, CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
        Assert.assertNotNull(tokenResponse.getBody().getValue());

    }

    @Test
    public void use_client_with_wrong_credentials() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_CLIENT_CREDENTIALS, CLIENT_ID_OAUTH2_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void use_client_with_wrong_grant_type() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, CLIENT_ID_OAUTH2_ID, COMMON_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void trying_to_login_with_not_exist_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);

    }

    private ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return action.restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
