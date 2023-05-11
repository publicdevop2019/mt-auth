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
import com.hw.helper.utility.RandomUtility;
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
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_client_then_login() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        client.setId(exchange.getHeaders().getLocation().toString());
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(client, tenantContext.getCreator(),
                tenantContext);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void resource_client_must_be_accessible() {
        Client client = ClientUtility.getClientAsResource(CLIENT_ID_TEST_ID);
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void resource_client_must_exist() {
        Client client = ClientUtility.getClientAsNonResource(UUID.randomUUID().toString());
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_can_read_client_list() {
        Client client = ClientUtility.getClientAsNonResource();
        ClientUtility.createTenantClient(tenantContext, client);
        ResponseEntity<SumTotal<Client>> response =
            ClientUtility.readTenantClients(tenantContext);
        Assert.assertNotSame(0, response.getBody().getData().size());
    }

    @Test
    public void client_password_will_not_change_when_update_value_empty() {
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD));
        client.setAccessTokenValiditySeconds(60);
        String oldSecret = client.getClientSecret();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setId(client1.getHeaders().getLocation().toString());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);

        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        client.setClientSecret(oldSecret);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client,
                tenantContext.getCreator(), tenantContext);
        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void replace_client_w_same_value_multiple_time_will_not_increase_version() {
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setClientSecret(" ");
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(tenantClient.getHeaders().getLocation().toString());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Void> client3 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client3.getStatusCode());
        ResponseEntity<Client> clientResponseEntity =
            ClientUtility.readTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, clientResponseEntity.getStatusCode());
        Assert.assertEquals(0, (int) clientResponseEntity.getBody().getVersion());
    }

    @Test
    public void create_client_then_update_it_to_be_resource() {
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD));
        client.setAccessTokenValiditySeconds(60);
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setResourceIndicator(true);
        client.setClientSecret(RandomUtility.randomStringWithNum());
        client.setId(client1.getHeaders().getLocation().toString());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client, tenantContext.getCreator(), tenantContext);
        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void client_type_cannot_change() {
        Client client = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP));
        client.setId(client1.getHeaders().getLocation().toString());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client3.getStatusCode());
        Assert.assertTrue(client3.getBody().getTypes().contains(ClientType.BACKEND_APP));

    }

    @Test
    public void change_client_secret() {
        Client client = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD));
        client.setAccessTokenValiditySeconds(60);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setId(client1.getHeaders().getLocation().toString());
        client.setClientSecret(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client, tenantContext.getCreator(), tenantContext);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void delete_client() {
        Client client = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(client1.getHeaders().getLocation().toString());
        ResponseEntity<Void> client2 = ClientUtility.deleteTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, client3.getStatusCode());
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
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Void.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_delete_resource_client()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        Client clientAsResource2 = ClientUtility.getClientAsResource();
        clientAsResource2.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource2);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(client.getHeaders().getLocation().toString());
        clientAsResource2.setId(client4.getHeaders().getLocation().toString());
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(20*1000);
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(clientAsResource.getId(),clientAsResource2.getId());
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(client1.getHeaders().getLocation().toString());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),"get"+"/"+RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //remove resource client
        ResponseEntity<Void> client2 = ClientUtility.deleteTenantClient(tenantContext, clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<User>> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource,tenantContext);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(),tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<Void> request5 = new HttpEntity<>(headers5);
        ResponseEntity<String> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_resource_client_is_not_accessible()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        Client clientAsResource2 = ClientUtility.getClientAsResource();
        clientAsResource2.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource2);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(client.getHeaders().getLocation().toString());
        clientAsResource2.setId(client4.getHeaders().getLocation().toString());
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(20*1000);
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(clientAsResource.getId(),clientAsResource2.getId());
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(client1.getHeaders().getLocation().toString());
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),"get"+"/"+RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //update resource client to remove access
        clientAsResource.setResourceIndicator(false);
        ResponseEntity<Void> exchange1 =
            ClientUtility.updateTenantClient(tenantContext, clientAsResource);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<User>> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource,tenantContext);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(),tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<Void> request5 = new HttpEntity<>(headers5);
        ResponseEntity<String> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());

    }

    @Test
    public void client_and_its_endpoint_should_be_deleted() throws InterruptedException {
        //create backend client
        Client randomClient = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = client.getHeaders().getLocation().toString();
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomGetEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        String endpointId = tenantEndpoint.getHeaders().getLocation().toString();
        //delete client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, randomClient);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(10000);
        //wait sometime and read endpoint again
        randomEndpointObj.setId(endpointId);
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, endpointResponseEntity.getStatusCode());
    }

    @Test
    public void client_validation_should_work() {
        //1. backend client requires external url
        Client randomClientObj = ClientUtility.createRandomClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, randomClientObj);
        randomClientObj.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        randomClientObj.setExternalUrl(null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantClient.getStatusCode());
        //2. backend client requires path
        Client randomClientObj2 = ClientUtility.createRandomClientObj();
        randomClientObj.setPath(null);
        randomClientObj2.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        ResponseEntity<Void> tenantClient2 =
            ClientUtility.createTenantClient(tenantContext, randomClientObj2);
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