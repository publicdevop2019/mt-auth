package com.hw.integration.identityaccess.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.*;

import static com.hw.helper.UserAction.*;
import static com.hw.integration.identityaccess.oauth2.BIzUserTest.RESOURCE_OWNER;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RefreshTokenTest {
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
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
    public void refresh_token_should_work() throws InterruptedException {
        //create client supports refresh token
        Client clientRaw = action.getClientRaw();
        String clientSecret = clientRaw.getClientSecret();
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientRaw.setResourceIds(Collections.singleton(CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.BACKEND_APP)));
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<String> client = action.createClient(clientRaw);
        String clientId = client.getHeaders().getLocation().toString();
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        //access endpoint
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER ;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ResourceOwner>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Thread.sleep(60000+60000+2000);//spring cloud gateway add 60S leeway
        //access access token should expire
        ResponseEntity<SumTotal<ResourceOwner>> exchange2 = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get access token with refresh token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("scope", "not_used");
        params.add("refresh_token", jwtPasswordWithClient.getBody().getRefreshToken().getValue());
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBasicAuth(clientId, clientSecret);
        headers2.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(params, headers2);
        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = action.restTemplate.exchange(PROXY_URL_TOKEN, HttpMethod.POST, request2, DefaultOAuth2AccessToken.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //use new access token for api call
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(exchange1.getBody().getValue());
        HttpEntity<String> request3 = new HttpEntity<>(null, headers3);
        ResponseEntity<SumTotal<ResourceOwner>> exchange3 = action.restTemplate.exchange(url, HttpMethod.GET, request3, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void refresh_token_should_have_exp(){
        //create client supports refresh token
        Client clientRaw = action.getClientRaw();
        String clientSecret = clientRaw.getClientSecret();
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientRaw.setResourceIds(Collections.singleton(CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.FIRST_PARTY)));
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<String> client = action.createClient(clientRaw);
        String clientId = client.getHeaders().getLocation().toString();
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        OAuth2RefreshToken refreshToken = jwtPasswordWithClient.getBody().getRefreshToken();
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
            Map<String, Object> var0 = mapper.readValue(s, new TypeReference<Map<String, Object>>() {
            });
            exp = (Integer)var0.get("exp");
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find authorities in authorization header");
        }
        Assert.assertNotNull(exp);
    }


}


































