package com.hw.integration.single.access.tenant;

import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_ADMIN;
import static com.hw.helper.AppConstant.CLIENTS;
import static com.hw.helper.AppConstant.CLIENT_ID_OAUTH2_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_RESOURCE_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_TEST_ID;
import static com.hw.helper.AppConstant.TEST_REDIRECT_URL;

import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.Endpoint;
import com.hw.helper.GrantType;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.UserTest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
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

@RunWith(SpringRunner.class)
@Slf4j
public class TenantClientTest extends TenantTest {


    @Test
    public void frontend_type_client_can_not_be_resource() {
        Client client = ClientUtility.getClientAsResource();
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP));
        ResponseEntity<Void> exchange = ClientUtility.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_client_then_login() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> exchange = ClientUtility.createClient(client);

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
    public void resource_client_must_be_accessible() {
        Client client = ClientUtility.getClientAsResource(CLIENT_ID_TEST_ID);
        ResponseEntity<Void> exchange = ClientUtility.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void resource_client_must_exist() {
        Client client = ClientUtility.getClientAsNonResource(UUID.randomUUID().toString());
        ResponseEntity<Void> exchange = ClientUtility.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_can_read_client_list() {
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
    public void client_password_will_not_change_when_update_value_empty() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
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
    public void replace_client_w_same_value_multiple_time_will_not_increase_version() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        oldClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
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

    @Test
    public void create_client_then_update_it_to_be_resource() {
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
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
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
    public void client_type_cannot_change() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
        Client newClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        newClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<Client> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertTrue(exchange2.getBody().getTypes().contains(ClientType.BACKEND_APP));

    }

    @Test
    public void change_client_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = ClientUtility.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
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
        ResponseEntity<Void> client1 = ClientUtility.createClient(oldClient);
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
    public void reserved_client_is_not_deletable() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/0C8AZTODP4HT");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_delete_resource_client()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        clientAsResource.setName("resource client");
        ResponseEntity<Void> client = ClientUtility.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        clientAsNonResource.setName("non resource client");
        ResponseEntity<Void> client1 = ClientUtility.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getAccessUrl(UserTest.USER_MGMT);
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
        ResponseEntity<Void> client = ClientUtility.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client1 = ClientUtility.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getOAuth2PasswordToken(clientId, clientSecret,
                ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getAccessUrl(UserTest.USER_MGMT);
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

    @Test
    public void client_and_its_endpoint_should_be_deleted() throws InterruptedException {
        //create backend client
        Client randomClient = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext.getCreator(), randomClient,
                tenantContext.getProject().getId());
        String clientId = client.getHeaders().getLocation().toString();
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomGetEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), randomEndpointObj,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        String endpointId = tenantEndpoint.getHeaders().getLocation().toString();
        //delete client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext.getCreator(), randomClient,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(10000);
        //wait sometime and read endpoint again

        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext.getCreator(), endpointId,
                tenantContext.getProject().getId());

        Assert.assertEquals(HttpStatus.OK, endpointResponseEntity.getStatusCode());
        Assert.assertNull(endpointResponseEntity.getBody());
    }

    @Test
    public void client_validation_should_work() {
        //1. backend client requires external url
        Client randomClientObj = ClientUtility.createRandomClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext.getCreator(), randomClientObj,
                tenantContext.getProject().getId());
        randomClientObj.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        randomClientObj.setExternalUrl(null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantClient.getStatusCode());
        //2. backend client requires path
        Client randomClientObj2 = ClientUtility.createRandomClientObj();
        randomClientObj.setPath(null);
        randomClientObj2.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        ResponseEntity<Void> tenantClient2 =
            ClientUtility.createTenantClient(tenantContext.getCreator(), randomClientObj2,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantClient2.getStatusCode());
    }

    @Test
    public void create_frontend_client_for_single_sign_on() {
        String login = UserUtility.login(tenantContext.getCreator());
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(tenantContext.getProject().getId(),
                tenantContext.getLoginClientId(), login, TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getOAuth2AuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                TEST_REDIRECT_URL, tenantContext.getLoginClientId(), "");
        Assert.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }
}