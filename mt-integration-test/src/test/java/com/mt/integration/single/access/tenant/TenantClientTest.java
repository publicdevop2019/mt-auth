package com.mt.integration.single.access.tenant;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.ClientType;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.GrantType;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantClientTest{
    private static TenantContext tenantContext;
    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }

    @Test
    public void tenant_frontend_type_client_can_not_be_resource() {
        Client client = ClientUtility.getClientAsResource();
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_create_client_then_login() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        client.setId(HttpUtility.getId(exchange));
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(client, tenantContext.getCreator(),
                tenantContext);

        Assertions.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assertions.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void tenant_resource_client_must_be_accessible() {
        Client client = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_TEST_ID);
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_resource_client_must_exist() {
        Client client = ClientUtility.getClientAsNonResource(UUID.randomUUID().toString());
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_can_read_client_list() {
        Client client = ClientUtility.getClientAsNonResource();
        ClientUtility.createTenantClient(tenantContext, client);
        ResponseEntity<SumTotal<Client>> response =
            ClientUtility.readTenantClients(tenantContext);
        Assertions.assertNotSame(0, response.getBody().getData().size());
    }

    @Test
    public void tenant_client_password_will_not_change_when_update_value_empty() {
        Client client = ClientUtility.createValidBackendClient();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        String oldSecret = client.getClientSecret();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setId(HttpUtility.getId(client1));
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);

        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        client.setClientSecret(oldSecret);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client,
                tenantContext.getCreator(), tenantContext);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assertions.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void tenant_create_client_then_update_it_to_be_resource() {
        Client client = ClientUtility.createValidBackendClient();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setResourceIndicator(true);
        client.setClientSecret(RandomUtility.randomStringWithNum());
        client.setId(HttpUtility.getId(client1));
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client, tenantContext.getCreator(), tenantContext);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assertions.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void tenant_client_type_cannot_change() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(client1));
        client.setClientSecret(" ");
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client3.getStatusCode());
        Assertions.assertTrue(client3.getBody().getTypes().contains(ClientType.BACKEND_APP.name()));

    }

    @Test
    public void tenant_change_client_secret() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setId(HttpUtility.getId(client1));
        client.setClientSecret(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(
                client, tenantContext.getCreator(), tenantContext);

        Assertions.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assertions.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void tenant_delete_client() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(client1));
        ResponseEntity<Void> client2 = ClientUtility.deleteTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, client3.getStatusCode());
    }

    @Test
    public void tenant_reserved_client_is_not_deletable() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String url =
            HttpUtility.getAccessUrl(AppConstant.CLIENTS + "/0C8AZTODP4HT");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Void.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_create_resource_client_and_client_which_access_it_then_delete_resource_client()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        Client clientAsResource2 = ClientUtility.getClientAsResource();
        clientAsResource2.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource2);
        Assertions.assertEquals(HttpStatus.OK, client.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(HttpUtility.getId(client));
        clientAsResource2.setId(HttpUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        TestUtility.proxyDefaultWait();
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(clientAsResource.getId(),
                clientAsResource2.getId());
        HashSet<String> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD.name());
        enums.add(GrantType.REFRESH_TOKEN.name());
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, clientAsNonResource);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(HttpUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = HttpUtility.getTenantUrl(clientAsResource2.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //remove resource client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, clientAsResource);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        TestUtility.proxyDefaultWait();
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource, tenantContext);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(), tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<Void> request5 = new HttpEntity<>(headers5);
        ResponseEntity<String> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

    @Test
    public void tenant_create_resource_client_and_client_which_access_it_then_resource_client_is_not_accessible()
        throws InterruptedException {
        Client clientAsResource = ClientUtility.getClientAsResource();
        Client clientAsResource2 = ClientUtility.getClientAsResource();
        clientAsResource2.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, clientAsResource);
        ResponseEntity<Void> client4 =
            ClientUtility.createTenantClient(tenantContext, clientAsResource2);
        Assertions.assertEquals(HttpStatus.OK, client.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, client4.getStatusCode());
        clientAsResource.setId(HttpUtility.getId(client));
        clientAsResource2.setId(HttpUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(5 * 1000);
        Client clientAsNonResource =
            ClientUtility.getClientAsNonResource(clientAsResource.getId(),
                clientAsResource2.getId());
        HashSet<String> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD.name());
        enums.add(GrantType.REFRESH_TOKEN.name());
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, clientAsNonResource);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(HttpUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = HttpUtility.getTenantUrl(clientAsResource2.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //update resource client to remove access
        clientAsResource.setResourceIndicator(false);
        ResponseEntity<Void> exchange1 =
            ClientUtility.updateTenantClient(tenantContext, clientAsResource);
        Assertions.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(5*1000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource, tenantContext);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(), tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<Void> request5 = new HttpEntity<>(headers5);
        ResponseEntity<String> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange5.getStatusCode());

    }

    @Test
    public void tenant_client_and_its_endpoint_should_be_deleted() throws InterruptedException {
        //create backend client
        Client randomClient = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = HttpUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        String endpointId = HttpUtility.getId(tenantEndpoint);
        //delete client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, randomClient);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(5*1000);
        //wait sometime and read endpoint again
        randomEndpointObj.setId(endpointId);
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, endpointResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_client_validation_should_work() {
        //1. backend client requires external url
        Client randomClientObj = ClientUtility.createRandomClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, randomClientObj);
        randomClientObj.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        randomClientObj.setExternalUrl(null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tenantClient.getStatusCode());
        //2. backend client requires path
        Client randomClientObj2 = ClientUtility.createRandomClientObj();
        randomClientObj.setPath(null);
        randomClientObj2.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        ResponseEntity<Void> tenantClient2 =
            ClientUtility.createTenantClient(tenantContext, randomClientObj2);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tenantClient2.getStatusCode());
    }

    @Test
    public void tenant_create_frontend_client_for_single_sign_on() {
        String login = UserUtility.login(tenantContext.getCreator());
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(tenantContext.getProject().getId(),
                tenantContext.getLoginClientId(), login, AppConstant.TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getOAuth2AuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, tenantContext.getLoginClientId(), "");
        Assertions.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }
}