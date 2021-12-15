package com.hw.integration.identityaccess.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static com.hw.helper.UserAction.*;

/**
 * this integration auth requires oauth2service to be running
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BizClientApiSecurityTest {
    @Autowired
    private UserAction action;
    public ObjectMapper mapper = new ObjectMapper();
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
    public void should_not_able_to_create_client_w_admin_account_when_going_through_proxy() throws JsonProcessingException {
        Client client = getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + "/clients/root";
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN, CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s = mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    /**
     * @return different GRANT_TYPE_PASSWORD client obj
     */
    private Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setGrantedAuthorities(Arrays.asList(AccessConstant.BACKEND_ID));
        client.setResourceIndicator(false);
        return client;
    }

    private Client getClientRaw(String... resourceIds) {
        Client client = new Client();
        client.setClientSecret(UUID.randomUUID().toString().replace("-", ""));
        client.setGrantTypeEnums(new HashSet<>(Arrays.asList(GrantTypeEnum.PASSWORD)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setResourceIds(new HashSet<>(Arrays.asList(resourceIds)));
        return client;
    }

    private ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String username, String userPwd, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return action.restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
