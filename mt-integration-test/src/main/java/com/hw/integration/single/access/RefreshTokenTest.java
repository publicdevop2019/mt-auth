package com.hw.integration.single.access;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.helper.AppConstant;
import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.Endpoint;
import com.hw.helper.GrantType;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.integration.single.access.tenant.TenantTest;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class RefreshTokenTest extends TenantTest {

    @Test
    public void refresh_token_should_work() throws InterruptedException {
        //create resource client and it's endpoints
        Client clientAsResource = ClientUtility.getClientAsResource();
        clientAsResource.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(client4.getHeaders().getLocation().toString());
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(10*1000);

        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw(clientAsResource.getId());
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setPath(RandomUtility.randomStringNoNum());
        clientRaw.setExternalUrl(RandomUtility.randomLocalHostUrl());
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.BACKEND_APP)));
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<Void> client = ClientUtility.createTenantClient(tenantContext, clientRaw);
        clientRaw.setId(client.getHeaders().getLocation().toString());
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> token = OAuth2Utility
            .getTenantPasswordToken(clientRaw,
                tenantContext.getCreator(), tenantContext);
        Assert.assertEquals(HttpStatus.OK, token.getStatusCode());
        //access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource.getPath(),"get"+"/"+RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getBody().getValue());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Thread.sleep(60000 + 60000 + 2000);//spring cloud gateway add 60S leeway
        //access token should expire
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get access token with refresh token

        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getTenantRefreshToken(token.getBody().getRefreshToken().getValue(),
                clientRaw, tenantContext);

        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //use new access token for api call
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(exchange1.getBody().getValue());
        HttpEntity<String> request3 = new HttpEntity<>(null, headers3);
        ResponseEntity<String> exchange3 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request3, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void refresh_token_should_have_exp(){
        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw();
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientRaw.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        clientRaw.setResourceIds(Collections.singleton(AppConstant.CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        clientRaw.setPath(RandomUtility.randomStringNoNum());
        clientRaw.setExternalUrl(RandomUtility.randomLocalHostUrl());
        ResponseEntity<Void> client = ClientUtility.createTenantClient(tenantContext, clientRaw);
        clientRaw.setId(client.getHeaders().getLocation().toString());
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientRaw,tenantContext.getCreator()
                , tenantContext);
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
            Map<String, Object> var0 =
                TestContext.mapper.readValue(s, new TypeReference<Map<String, Object>>() {
                });
            exp = (Integer) var0.get("exp");
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "unable to find authorities in authorization header");
        }
        Assert.assertNotNull(exp);
    }


}

































