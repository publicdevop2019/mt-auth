package com.hw.integration.identityaccess.oauth2;

import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_ADMIN;
import static com.hw.helper.AppConstant.CLIENTS;
import static com.hw.helper.AppConstant.CLIENT_ID_OAUTH2_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_RESOURCE_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_TEST_ID;
import static com.hw.helper.AppConstant.PROXY_URL_TOKEN;
import static com.hw.helper.utility.TestContext.mapper;
import static com.hw.integration.identityaccess.oauth2.UserTest.USER_MNGMT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.GrantTypeEnum;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@Slf4j
public class ClientTest {

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
    public void only_client_w_first_party_n_backend_role_can_be_create_as_resource() {
        TestContext.init();
        Client client = ClientUtility.getInvalidClientAsResource();
        ResponseEntity<String> exchange = ClientUtility.createClient(client);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_non_resource_client_with_valid_resource_ids_then_able_to_use_this_client_to_login() {
        Client client = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> exchange = ClientUtility.createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                exchange.getHeaders().getLocation().toString(), client.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void create_client_which_is_resource_itself_with_valid_resource_ids_then_able_to_use_this_client_to_login() {
        Client client = ClientUtility.getClientAsResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> exchange = ClientUtility.createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                exchange.getHeaders().getLocation().toString(), client.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void should_not_able_to_create_client_which_is_resource_itself_with_wrong_resource_ids() {
        Client client = ClientUtility.getClientAsResource(CLIENT_ID_TEST_ID);
        ResponseEntity<String> exchange = ClientUtility.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void should_not_able_to_create_client_which_is_resource_itself_with_wrong_not_existing_resource_ids() {
        Client client = ClientUtility.getClientAsNonResource(UUID.randomUUID().toString());
        ResponseEntity<String> exchange = ClientUtility.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void admin_account_can_read_client() {
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordAdmin =
            UserUtility.getJwtPasswordAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordAdmin.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(CLIENTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }

    @Test
    public void create_client_then_replace_it_with_different_client_only_password_is_empty_then_login_with_new_client_but_password_should_be_old_one() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        Client newClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        newClient.setClientSecret(" ");
        newClient.setVersion(0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                client1.getHeaders().getLocation().toString(), oldClient.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void create_client_then_replace_it_with_same_client_info_only_password_is_empty_N_times_then_client_version_should_not_increase() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        oldClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        ResponseEntity<Client> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, Client.class);

        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Assert.assertEquals(0, (int) exchange1.getBody().getVersion());
    }

    @SuppressWarnings("checkstyle:ParenPad")
    @Test
    public void create_client_then_update_it_tobe_resource() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Client oldClient = ClientUtility.getClientAsResource(CLIENT_ID_RESOURCE_ID);
        oldClient.setResourceIndicator(true);
        oldClient.setClientSecret(" ");
        oldClient.setVersion(0);
        HttpHeaders headers = new HttpHeaders();
        String bearer = tokenResponse.getBody().getValue();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String clientSecret = oldClient.getClientSecret();

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                client1.getHeaders().getLocation().toString(), clientSecret, ACCOUNT_USERNAME_ADMIN,
                ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void should_not_be_able_to_update_client_type_once_created()
        throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        Client newClient = ClientUtility.getInvalidClientAsResource(CLIENT_ID_RESOURCE_ID);
        newClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s1 = mapper.writeValueAsString(newClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<Client> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertTrue(exchange2.getBody().getTypes().contains(ClientType.BACKEND_APP));
        Assert.assertTrue(exchange2.getBody().getTypes().contains(ClientType.FIRST_PARTY));

    }

    @Test
    public void create_client_then_update_it_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        Client newClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        newClient.setVersion(0);
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                client1.getHeaders().getLocation().toString(), newClient.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void delete_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                client1.getHeaders().getLocation().toString(), oldClient.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse1.getStatusCode());
    }

    @Test
    public void root_client_is_not_deletable() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        /**
         * add ROLE_ROOT so it can not be deleted
         */
        oldClient.setTypes(
            new HashSet<>(Arrays.asList(ClientType.BACKEND_APP, ClientType.ROOT_APPLICATION)));
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getOAuth2PasswordToken(
                client1.getHeaders().getLocation().toString(), oldClient.getClientSecret(),
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_delete_resource_client()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        clientAsResource.setName("resource client");
        ResponseEntity<String> client = ClientUtility.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        clientAsNonResource.setName("non resource client");
        ResponseEntity<String> client1 = ClientUtility.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getAccessUrl(USER_MNGMT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //delete resource client
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String url4 =
            UrlUtility.getAccessUrl(CLIENTS + "/" + resourceClientId);
        HttpHeaders headers4 = new HttpHeaders();
        headers4.setBearerAuth(bearer);
        HttpEntity<String> request4 = new HttpEntity<>(null, headers4);
        ResponseEntity<String> exchange1 =
            TestContext.getRestTemplate().exchange(url4, HttpMethod.DELETE, request4, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource should not have removed client
        String url5 = UrlUtility.getAccessUrl(CLIENTS + "/" + clientId);
        ResponseEntity<Client> exchange3 =
            TestContext.getRestTemplate().exchange(url5, HttpMethod.GET, request4, Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Set<String> resourceIds = exchange3.getBody().getResourceIds();
        Assert.assertEquals(1, resourceIds.size());
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<User>> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getRefreshTokenResponse(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientId, clientSecret);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<String> request5 = new HttpEntity<>(null, headers5);
        ResponseEntity<SumTotal<User>> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_resource_client_is_not_accessible()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        ResponseEntity<String> client = ClientUtility.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<String> client1 = ClientUtility.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getAccessUrl(USER_MNGMT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //update resource client to remove access
        clientAsResource.setResourceIndicator(false);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String url4 =
            UrlUtility.getAccessUrl(CLIENTS + "/" + resourceClientId);
        HttpHeaders headers4 = new HttpHeaders();
        headers4.setBearerAuth(bearer);
        HttpEntity<Client> request4 = new HttpEntity<>(clientAsResource, headers4);
        ResponseEntity<String> exchange1 =
            TestContext.getRestTemplate().exchange(url4, HttpMethod.PUT, request4, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<User>> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getRefreshTokenResponse(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientId, clientSecret);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<String> request5 = new HttpEntity<>(null, headers5);
        ResponseEntity<SumTotal<User>> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

}