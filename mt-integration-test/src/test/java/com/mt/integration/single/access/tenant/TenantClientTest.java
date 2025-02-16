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
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
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
import org.junit.jupiter.api.TestInfo;
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
public class TenantClientTest {
    private static TenantContext tenantContext;

    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void tenant_frontend_type_client_can_not_be_resource() {
        Client client = ClientUtility.getClientAsResource();
        client.setType(ClientType.FRONTEND_APP.name());
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void tenant_create_backend_client_then_single_sign_on() {
        Client backendClient = ClientUtility.getClientAsNonResource();
        backendClient.setGrantTypeEnums(Collections.singleton(GrantType.AUTHORIZATION_CODE.name()));
        backendClient.setRegisteredRedirectUri(
            Collections.singleton(AppConstant.TEST_REDIRECT_URL));
        ResponseEntity<Void> exchange =
            ClientUtility.createTenantClient(tenantContext, backendClient);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        backendClient.setId(HttpUtility.getId(exchange));

        String login = UserUtility.emailPwdLogin(tenantContext.getCreator());
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(tenantContext.getProject().getId(),
                backendClient.getId(), login, AppConstant.TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getAuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, backendClient.getId(),
                backendClient.getClientSecret());
        Assertions.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }

    @Test
    public void tenant_cannot_create_client_then_login_w_password() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        client.setId(HttpUtility.getId(exchange));
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(client, tenantContext.getCreator(),
                tenantContext);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse1.getStatusCode());
    }

    @Test
    public void tenant_cannot_create_client_w_password_grant() {
        Client client = ClientUtility.getClientAsNonResource();
        client.setGrantTypeEnums(
            new HashSet<>(Collections.singletonList(GrantType.PASSWORD.name())));
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }


    @Test
    public void tenant_resource_client_must_be_accessible() {
        Client client =
            ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_TEST_ID_NONE_RESOURCE);
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
    public void tenant_create_client_then_update_it_to_be_resource() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setId(HttpUtility.getId(client1));

        client.setResourceIndicator(true);
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void tenant_client_type_cannot_change() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(client1));
        client.setClientSecret(client.getClientSecret());
        client.setType(ClientType.FRONTEND_APP.name());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client3.getStatusCode());
        Assertions.assertTrue(
            client3.getBody().getType().equalsIgnoreCase(ClientType.BACKEND_APP.name()));

    }

    @Test
    public void tenant_change_client_secret() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(client1));
        client.setClientSecret(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
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
    public void tenant_create_resource_client_and_client_which_access_it_then_delete_resource_client() {
        //create resource client with 1 endpoint
        Client resourceClient = ClientUtility.getClientAsResource();
        resourceClient.setExternalUrl("http://localhost:9999");
        Client resourceClient2 = ClientUtility.getClientAsResource();
        ResponseEntity<Void> response =
            ClientUtility.createTenantClient(tenantContext, resourceClient);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, resourceClient2);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        resourceClient.setId(HttpUtility.getId(response));
        resourceClient2.setId(HttpUtility.getId(response2));
        //create resource client's endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(resourceClient.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());

        TestUtility.proxyDefaultWait();

        Client accessClient =
            ClientUtility.createAuthorizationClientObj();
        HashSet<String> strings = new HashSet<>();
        strings.add(resourceClient.getId());
        strings.add(resourceClient2.getId());
        accessClient.setResourceIds(strings);
        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, accessClient);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        accessClient.setId(HttpUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> userTokenResp =
            UserUtility.userLoginToTenant(tenantContext.getProject(), accessClient,
                tenantContext.getCreator());
        Assertions.assertEquals(HttpStatus.OK, userTokenResp.getStatusCode());
        // accessClient can access endpoint
        String url = HttpUtility.getTenantUrl(resourceClient.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userTokenResp.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        //remove resource client 2, so endpoint will not be deleted
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, resourceClient2);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        TestUtility.proxyDefaultWait();

        //accessClient can not access endpoint both access token
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> userTokenResp2 =
            UserUtility.userLoginToTenant(tenantContext.getProject(), accessClient,
                tenantContext.getCreator());
        Assertions.assertEquals(HttpStatus.OK, userTokenResp2.getStatusCode());
        // accessClient can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(userTokenResp2.getBody().getValue());
        HttpEntity<Void> request5 = new HttpEntity<>(headers5);
        ResponseEntity<String> exchange5 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request5, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

    @Test
    public void tenant_create_resource_client_and_client_which_access_it_then_resource_client_is_not_accessible()
        throws InterruptedException {
        //create resource client with 1 endpoint
        Client resourceClient = ClientUtility.getClientAsResource();
        resourceClient.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> response =
            ClientUtility.createTenantClient(tenantContext, resourceClient);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        resourceClient.setId(HttpUtility.getId(response));
        //create resource client's endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(resourceClient.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());

        TestUtility.proxyDefaultWait();

        Client accessClient =
            ClientUtility.createAuthorizationClientObj();
        accessClient.setResourceIds(Collections.singleton(resourceClient.getId()));

        ResponseEntity<Void> client1 =
            ClientUtility.createTenantClient(tenantContext, accessClient);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        accessClient.setId(HttpUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> userTokenResp =
            UserUtility.userLoginToTenant(tenantContext.getProject(), accessClient,
                tenantContext.getCreator());
        Assertions.assertEquals(HttpStatus.OK, userTokenResp.getStatusCode());
        // accessClient can access endpoint
        String url = HttpUtility.getTenantUrl(resourceClient.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userTokenResp.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //update resource client to remove access
        resourceClient.setResourceIndicator(false);
        ResponseEntity<Void> exchange1 =
            ClientUtility.updateTenantClient(tenantContext, resourceClient);
        Assertions.assertEquals(HttpStatus.OK, exchange1.getStatusCode());

        Thread.sleep(5 * 1000);
        //accessClient can not access endpoint both access token
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> userTokenResp2 =
            UserUtility.userLoginToTenant(tenantContext.getProject(), accessClient,
                tenantContext.getCreator());
        Assertions.assertEquals(HttpStatus.OK, userTokenResp2.getStatusCode());
        // accessClient can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(userTokenResp2.getBody().getValue());
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
        Thread.sleep(5 * 1000);
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
        randomClientObj.setType(ClientType.BACKEND_APP.name());
        randomClientObj.setExternalUrl(null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tenantClient.getStatusCode());
        //2. backend client requires path
        Client randomClientObj2 = ClientUtility.createRandomClientObj();
        randomClientObj.setPath(null);
        randomClientObj2.setType(ClientType.BACKEND_APP.name());
        ResponseEntity<Void> tenantClient2 =
            ClientUtility.createTenantClient(tenantContext, randomClientObj2);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tenantClient2.getStatusCode());
    }

    @Test
    public void tenant_create_frontend_client_then_single_sign_on() {
        String login = UserUtility.emailPwdLogin(tenantContext.getCreator());
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(tenantContext.getProject().getId(),
                tenantContext.getLoginClient().getId(), login, AppConstant.TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getAuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, tenantContext.getLoginClient().getId(),
                tenantContext.getLoginClient().getClientSecret());
        Assertions.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }
}