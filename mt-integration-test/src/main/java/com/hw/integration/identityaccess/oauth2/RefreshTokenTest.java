package com.hw.integration.identityaccess.oauth2;

import static com.hw.integration.identityaccess.oauth2.UserTest.USER_MGMT;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.helper.AppConstant;
import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.GrantTypeEnum;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
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
public class RefreshTokenTest {
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, id {}", description.getMethodName(),
                TestContext.getTestId());
        }
    };

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    public void refresh_token_should_work() throws InterruptedException {
        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw();
        String clientSecret = clientRaw.getClientSecret();
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientRaw.setResourceIds(Collections.singleton(AppConstant.CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setTypes(new HashSet<>(List.of(ClientType.BACKEND_APP)));
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<String> client = ClientUtility.createClient(clientRaw);
        String clientId = client.getHeaders().getLocation().toString();
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient = OAuth2Utility
            .getOAuth2PasswordToken(clientId, clientSecret,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        //access endpoint
        String url = UrlUtility.getAccessUrl(USER_MGMT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Thread.sleep(60000 + 60000 + 2000);//spring cloud gateway add 60S leeway
        //access access token should expire
        ResponseEntity<SumTotal<User>> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get access token with refresh token

        ResponseEntity<DefaultOAuth2AccessToken> exchange1 = OAuth2Utility
            .getRefreshTokenResponse(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientId, clientSecret);

        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //use new access token for api call
        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(exchange1.getBody().getValue());
        HttpEntity<String> request3 = new HttpEntity<>(null, headers3);
        ResponseEntity<SumTotal<User>> exchange3 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request3, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void refresh_token_should_have_exp() {
        //create client supports refresh token
        Client clientRaw = ClientUtility.getClientRaw();
        String clientSecret = clientRaw.getClientSecret();
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientRaw.setResourceIds(Collections.singleton(AppConstant.CLIENT_ID_OAUTH2_ID));
        clientRaw.setGrantTypeEnums(enums);
        clientRaw.setAccessTokenValiditySeconds(60);
        clientRaw.setRefreshTokenValiditySeconds(1000);
        ResponseEntity<String> client = ClientUtility.createClient(clientRaw);
        String clientId = client.getHeaders().getLocation().toString();
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
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


































