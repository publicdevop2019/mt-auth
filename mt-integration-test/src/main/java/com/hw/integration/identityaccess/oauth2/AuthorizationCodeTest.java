package com.hw.integration.identityaccess.oauth2;

import com.hw.helper.AccessConstant;
import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.ServiceUtility;
import com.hw.helper.UserAction;
import com.jayway.jsonpath.JsonPath;
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
@SpringBootTest
@Slf4j
public class AuthorizationCodeTest {

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
    public void should_get_authorize_code_after_pwd_login_for_user() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordUser();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> code = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String body = code.getBody();
        String read = JsonPath.read(body, "$.authorize_code");
        Assert.assertNotNull(read);
    }

    @Test
    public void should_authorize_token_has_permission() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordUser();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");

        Assert.assertNotNull(code);

        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(GRANT_TYPE_AUTHORIZATION_CODE, code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);

        Assert.assertEquals(HttpStatus.OK, authorizationToken.getStatusCode());
        Assert.assertNotNull(authorizationToken.getBody());
        DefaultOAuth2AccessToken body = authorizationToken.getBody();
        List<String> authorities = ServiceUtility.getPermissions(body.getValue());
        Assert.assertNotEquals(0, authorities.size());

    }


    @Test
    public void use_wrong_authorize_code_after_user_grant_access() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> code = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = action.getAuthorizationToken(UUID.randomUUID().toString(), OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_redirect_url_during_authorization() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = action.getAuthorizationToken(code, UUID.randomUUID().toString(), CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_grant_type_during_authorization() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(GRANT_TYPE_PASSWORD, code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_during_authorization() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = action.getAuthorizationToken(code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_w_credential_during_authorization() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(CLIENT_ID_OM_ID, accessToken, OBJECT_MARKET_REDIRECT_URI);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = action.getAuthorizationToken(code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_LOGIN_ID, UUID.randomUUID().toString());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void wrong_client_id_passed_during_authorization_code_call() {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = action.getJwtPasswordRoot();
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = action.getCodeResp(UUID.randomUUID().toString(), accessToken, OBJECT_MARKET_REDIRECT_URI);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, codeResp.getStatusCode());
    }

    private ResponseEntity<DefaultOAuth2AccessToken> getAuthorizationToken(String grantType, String code, String redirect_uri, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);
        params.add("scope", PROJECT_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return action.restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
