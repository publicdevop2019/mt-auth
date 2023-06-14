package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.ClientType;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.OAuth2Utility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_client_then_login() {
        Client client = ClientUtility.getClientAsNonResource();
        ResponseEntity<Void> exchange = ClientUtility.createTenantClient(tenantContext, client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        client.setId(UrlUtility.getId(exchange));
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            OAuth2Utility.getTenantPasswordToken(client, tenantContext.getCreator(),
                tenantContext);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void resource_client_must_be_accessible() {
        Client client = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_TEST_ID);
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
        Client client = ClientUtility.createValidBackendClient();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        String oldSecret = client.getClientSecret();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setId(UrlUtility.getId(client1));
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
    public void create_client_then_update_it_to_be_resource() {
        Client client = ClientUtility.createValidBackendClient();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setResourceIndicator(true);
        client.setClientSecret(RandomUtility.randomStringWithNum());
        client.setId(UrlUtility.getId(client1));
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
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        client.setId(UrlUtility.getId(client1));
        ResponseEntity<Void> client2 = ClientUtility.updateTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client3.getStatusCode());
        Assert.assertTrue(client3.getBody().getTypes().contains(ClientType.BACKEND_APP.name()));

    }

    @Test
    public void change_client_secret() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD.name()));
        client.setAccessTokenValiditySeconds(60);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        client.setId(UrlUtility.getId(client1));
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
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(client1));
        ResponseEntity<Void> client2 = ClientUtility.deleteTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        ResponseEntity<Client> client3 = ClientUtility.readTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, client3.getStatusCode());
    }

    @Test
    public void reserved_client_is_not_deletable() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String url =
            UrlUtility.getAccessUrl(AppConstant.CLIENTS + "/0C8AZTODP4HT");
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
        clientAsResource.setId(UrlUtility.getId(client));
        clientAsResource2.setId(UrlUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(20 * 1000);
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
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(UrlUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //remove resource client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource, tenantContext);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(), tenantContext);
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
        clientAsResource.setId(UrlUtility.getId(client));
        clientAsResource2.setId(UrlUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        Thread.sleep(20 * 1000);
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
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(UrlUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),
            "get" + "/" + RandomUtility.randomStringNoNum());
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
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = OAuth2Utility
            .getTenantRefreshToken(jwtPasswordWithClient.getBody().getRefreshToken().getValue(),
                clientAsNonResource, tenantContext);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource,
                tenantContext.getCreator(), tenantContext);
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
        Client randomClient = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        String endpointId = UrlUtility.getId(tenantEndpoint);
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
        randomClientObj.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        randomClientObj.setExternalUrl(null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantClient.getStatusCode());
        //2. backend client requires path
        Client randomClientObj2 = ClientUtility.createRandomClientObj();
        randomClientObj.setPath(null);
        randomClientObj2.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        ResponseEntity<Void> tenantClient2 =
            ClientUtility.createTenantClient(tenantContext, randomClientObj2);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantClient2.getStatusCode());
    }

    @Test
    public void create_frontend_client_for_single_sign_on() {
        String login = UserUtility.login(tenantContext.getCreator());
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(tenantContext.getProject().getId(),
                tenantContext.getLoginClientId(), login, AppConstant.TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getOAuth2AuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, tenantContext.getLoginClientId(), "");
        Assert.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }

    @Test
    public void validation_create_valid_backend() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    public void validation_create_valid_frontend() {
        Client client = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    public void validation_create_name() {
        Client client = ClientUtility.createValidBackendClient();
        //null
        client.setName(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        client.setName(" ");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        client.setName("");
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        client.setName("<");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //max length
        client.setName(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //min length
        client.setName("01");
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_description() {
        Client client = ClientUtility.createValidBackendClient();
        //blank
        client.setDescription(" ");
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        client.setDescription("");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid char
        client.setDescription("<");
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        client.setDescription(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_secret() {
        Client client = ClientUtility.createValidBackendClient();
        //type is backend and secret is missing
        client.setHasSecret(true);
        client.setClientSecret(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //type is frontend but secret is present
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        client.setClientSecret("test");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());

        //secret format
        client.setHasSecret(true);
        client.setClientSecret("0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, response5.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //other tenant's id
        project.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url = ClientUtility.getUrl(project);
        ResponseEntity<Void> response2 =
            Utility.createResource(tenantContext.getCreator(), url, client);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response2.getStatusCode());
        //blank
        project.setId(" ");
        String url2 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response3 =
            Utility.createResource(tenantContext.getCreator(), url2, client);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response3.getStatusCode());
        //empty
        project.setId("");
        String url3 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response4 =
            Utility.createResource(tenantContext.getCreator(), url3, client);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response4.getStatusCode());
        //wrong format
        project.setId("abc");
        String url4 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response5 =
            Utility.createResource(tenantContext.getCreator(), url4, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //null
        project.setId("null");
        String url5 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response6 =
            Utility.createResource(tenantContext.getCreator(), url5, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_path() {
        Client client = ClientUtility.createValidBackendClient();
        String repeatedPath = client.getPath();
        Client client1 = ClientUtility.createValidFrontendClient();

        //wrong path format
        client.setPath("/test/");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong path format
        client.setPath("/test-/");
        ResponseEntity<Void> response10 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
        //wrong path format
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
        //type is backend and path is missing
        client.setPath(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //type is frontend but path is present
        client1.setPath(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //max length
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //min length
        client.setPath(RandomUtility.randomStringNoNum().substring(0, 4));
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //unique across application
        client.setPath(repeatedPath);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, response8.getStatusCode());
        ResponseEntity<Void> response11 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response11.getStatusCode());
    }

    @Test
    public void validation_create_external_url() {
        Client client = ClientUtility.createValidBackendClient();

        Client client1 = ClientUtility.createValidFrontendClient();

        //externalUrl format is wrong
        client1.setExternalUrl(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //type is backend and externalUrl is missing
        client.setExternalUrl(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //type is frontend but externalUrl is present
        client1.setExternalUrl(RandomUtility.randomLocalHostUrl());
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //max length
        client1.setExternalUrl(RandomUtility.randomLocalHostUrl() +
            "/abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_grant_type() {
        Client client = ClientUtility.createValidBackendClient();

        Client client1 = ClientUtility.createAuthorizationClientObj();

        //grantType is empty
        client.setGrantTypeEnums(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //grantType is null
        client.setGrantTypeEnums(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //grantType invalid value
        client.setGrantTypeEnums(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //refresh requires password first
        client.setGrantTypeEnums(Collections.singleton(GrantType.REFRESH_TOKEN.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //authorization grant but registered redirect uri is empty
        client1.setRegisteredRedirectUri(Collections.emptySet());
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //authorization grant but registered redirect uri is null
        client1.setRegisteredRedirectUri(null);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //refresh grant but refresh token is 0
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        client.setGrantTypeEnums(strings);
        client.setRefreshTokenValiditySeconds(0);
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
        //refresh grant but refresh token is null
        client.setRefreshTokenValiditySeconds(null);
        ResponseEntity<Void> response10 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
    }

    @Test
    public void validation_create_type() {
        Client client = ClientUtility.createValidBackendClient();
        //type is null
        client.setTypes(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //type is empty
        client.setTypes(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //type is invalid
        client.setTypes(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //both type cannot present
        HashSet<String> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP.name());
        strings.add(ClientType.FRONTEND_APP.name());
        client.setTypes(strings);
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_access_token_validity_second() {
        Client client = ClientUtility.createValidBackendClient();
        //accessTokenValiditySeconds is null
        client.setAccessTokenValiditySeconds(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //accessTokenValiditySeconds is 0
        client.setAccessTokenValiditySeconds(0);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //value too large
        client.setAccessTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //value too small
        client.setAccessTokenValiditySeconds(30);
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }


    @Test
    public void validation_create_registered_redirect_url() {

        Client client = ClientUtility.createAuthorizationClientObj();
        //has value but not authorization grant
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //too many elements
        HashSet<String> urls = new HashSet<>();
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        client.setRegisteredRedirectUri(urls);
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_refresh_token_validity_second() {
        Client client = ClientUtility.createValidBackendClient();

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //has value but not password grant
        client.setRefreshTokenValiditySeconds(120);
        grantTypes.remove(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //value too large
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //value too small
        client.setRefreshTokenValiditySeconds(1);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_resource_ids() {
        Client client = ClientUtility.createValidBackendClient();
        //too many elements
        HashSet<String> strings = new HashSet<>();
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        client.setResourceIds(strings);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //format
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(RandomUtility.randomStringNoNum());
        client.setResourceIds(strings2);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //resource id that belong to another project
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(AppConstant.CLIENT_ID_TEST_ID);
        client.setResourceIds(strings3);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());

    }

    @Test
    public void validation_create_resource_indicator() {
        Client client = ClientUtility.createValidBackendClient();

        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //null
        client.setResourceIndicator(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //true but is frontend
        client1.setResourceIndicator(true);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_auto_approve() {
        Client client = ClientUtility.createAuthorizationClientObj();
        //missing when authorization grant
        client.setAutoApprove(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //present when not authorization grant and redirect url missing
        client.setAutoApprove(true);
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //present when not authorization grant
        client.setAutoApprove(true);
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomLocalHostUrl()));
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_project_id() {
        //mismatch
        //blank
        //empty
        //wrong format
        //null
    }

    @Test
    public void validation_update_has_secret() {
        //true but no secret
        //false but secret present
        //null but secret present

    }

    @Test
    public void validation_update_secret() {
        //type is backend and secret is missing
        //type is frontend but secret is present
        //secret format
        //secret will not change
    }

    @Test
    public void validation_update_description() {
        //blank
        //invalid char
        //max length
    }

    @Test
    public void validation_update_path() {
        //wrong path format
        //type is backend and path is missing
        //type is frontend but path is present
        //max length
        //min length
        //unique across application
    }

    @Test
    public void validation_update_external_url() {
        //externalUrl format is wrong
        //type is backend and externalUrl is missing
        //type is frontend but externalUrl is present
        //max length
    }

    @Test
    public void validation_update_grant_type() {
        //grantType is empty
        //grantType is null
        //grantType invalid value
        //refresh requires password first
        //authorization grant but registered redirect uri is empty
        //authorization grant but registered redirect uri is null
        //password grant but refresh token is 0
        //password grant but refresh token is null
    }

    @Test
    public void validation_update_access_token_validity_second() {
        //accessTokenValiditySeconds is null
        //accessTokenValiditySeconds is 0
        //value too large
        //value too small
    }


    @Test
    public void validation_update_registered_redirect_url() {
        //has value but not authorization grant
        //wrong format
        //too many elements
    }

    @Test
    public void validation_update_refresh_token_validity_second() {
        //has value but not password grant
        //value too large
        //value too small
    }

    @Test
    public void validation_update_resource_ids() {
        //too many elements
        //format
        //resource id that belong to another project

    }

    @Test
    public void validation_update_version() {
        //null
        //min value
        //max value
        //version mismatch
    }

    @Test
    public void validation_update_resource_indicator() {
        //null
        //true but is frontend
    }

    @Test
    public void validation_update_auto_approve() {
        //missing when authorization grant
        //present when not authorization grant
    }

    @Test
    public void validation_patch_project_id() {
        //mismatch
        //blank
        //empty
        //wrong format
        //null
    }

    @Test
    public void validation_patch_description() {
        //blank
        //invalid char
        //max length
    }

    @Test
    public void validation_patch_path() {
        //wrong path format
        //type is backend and path is missing
        //type is frontend but path is present
        //max length
        //min length
        //unique across application
    }

    @Test
    public void validation_patch_grant_type() {
        //grantType is empty
        //grantType is null
        //grantType invalid value
        //refresh requires password first
        //authorization grant but registered redirect uri is empty
        //authorization grant but registered redirect uri is null
        //password grant but refresh token is 0
        //password grant but refresh token is null
    }

    @Test
    public void validation_patch_access_token_validity_second() {
        //accessTokenValiditySeconds is null
        //accessTokenValiditySeconds is 0
        //value too large
        //value too small
    }

    @Test
    public void validation_patch_resource_ids() {
        //too many elements
        //format
        //resource id that belong to another project

    }

    @Test
    public void validation_patch_resource_indicator() {
        //null
        //true but is frontend
    }
}