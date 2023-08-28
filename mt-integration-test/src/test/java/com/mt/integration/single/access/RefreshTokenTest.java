package com.mt.integration.single.access;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.ClientType;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.GrantType;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.TestUtility;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void refresh_token_should_work() throws InterruptedException {
        //create resource client and it's endpoints
        Client clientAsResource = ClientUtility.getClientAsResource();
        clientAsResource.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        Assertions.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(HttpUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        TestUtility.proxyDefaultWait();

        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw(clientAsResource.getId());
        HashSet<String> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD.name());
        enums.add(GrantType.REFRESH_TOKEN.name());
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setPath(RandomUtility.randomStringNoNum());
        clientRaw.setExternalUrl(RandomUtility.randomLocalHostUrl());
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.BACKEND_APP.name())));
        clientRaw.setResourceIndicator(true);
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<Void> client = ClientUtility.createTenantClient(tenantContext, clientRaw);
        clientRaw.setId(HttpUtility.getId(client));
        Assertions.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> token = OAuth2Utility
            .getTenantPasswordToken(clientRaw,
                tenantContext.getCreator(), tenantContext);
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //access endpoint
        String url = HttpUtility.getTenantUrl(clientAsResource.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
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
            .getTenantRefreshToken(token.getBody().getRefreshToken().getValue(),
                clientRaw, tenantContext);

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
        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw();
        HashSet<String> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD.name());
        enums.add(GrantType.REFRESH_TOKEN.name());
        clientRaw.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        clientRaw.setResourceIds(Collections.singleton(AppConstant.CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setResourceIndicator(true);
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        clientRaw.setPath(RandomUtility.randomStringNoNum());
        clientRaw.setExternalUrl(RandomUtility.randomLocalHostUrl());
        ResponseEntity<Void> client = ClientUtility.createTenantClient(tenantContext, clientRaw);
        clientRaw.setId(HttpUtility.getId(client));
        Assertions.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientRaw, tenantContext.getCreator()
                , tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
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
            Map<String, Object> var0 =
                TestContext.mapper.readValue(s, new TypeReference<Map<String, Object>>() {
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
        //create resource client and it's endpoints
        Client clientAsResource = ClientUtility.getClientAsResource();
        clientAsResource.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        Assertions.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(HttpUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        TestUtility.proxyDefaultWait();

        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw(clientAsResource.getId());
        HashSet<String> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD.name());
        enums.add(GrantType.REFRESH_TOKEN.name());
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setPath(RandomUtility.randomStringNoNum());
        clientRaw.setExternalUrl(RandomUtility.randomLocalHostUrl());
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.BACKEND_APP.name())));
        clientRaw.setResourceIndicator(true);
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client = ClientUtility.createTenantClient(tenantContext, clientRaw);
        clientRaw.setId(HttpUtility.getId(client));
        Assertions.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> token = OAuth2Utility
            .getTenantPasswordToken(clientRaw,
                tenantContext.getCreator(), tenantContext);
        log.info("access token is {}", token.getBody().getValue());
        Assertions.assertEquals(HttpStatus.OK, token.getStatusCode());
        //access endpoint
        String url = HttpUtility.getTenantUrl(clientAsResource.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getBody().getValue());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Thread.sleep(2 * 60 * 1000 + 60 * 1000 + 2 * 1000);//spring cloud gateway add 60S leeway
        //access token should expire
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get access token with refresh token
        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getTenantRefreshToken(token.getBody().getRefreshToken().getValue(),
                clientRaw, tenantContext);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange1.getStatusCode());
    }


}


































