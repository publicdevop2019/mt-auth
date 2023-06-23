package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.ClientType;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.pojo.PatchCommand;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantClientTest extends TenantTest {


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
        client.setId(UrlUtility.getId(exchange));
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
        client.setId(UrlUtility.getId(client1));
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
        client.setId(UrlUtility.getId(client1));
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
        client.setId(UrlUtility.getId(client1));
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
        client.setId(UrlUtility.getId(client1));
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
        client.setId(UrlUtility.getId(client1));
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
            UrlUtility.getAccessUrl(AppConstant.CLIENTS + "/0C8AZTODP4HT");
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
        clientAsResource.setId(UrlUtility.getId(client));
        clientAsResource2.setId(UrlUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(UrlUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),
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
        Thread.sleep(10000);
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
        clientAsResource.setId(UrlUtility.getId(client));
        clientAsResource2.setId(UrlUtility.getId(client4));
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientAsResource2.getId());
        endpoint.setPath("get/**");
        endpoint.setMethod("GET");
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        clientAsNonResource.setId(UrlUtility.getId(client1));
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient =
            OAuth2Utility.getTenantPasswordToken(clientAsNonResource, tenantContext.getCreator(),
                tenantContext);
        Assertions.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UrlUtility.getTenantUrl(clientAsResource2.getPath(),
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
        Thread.sleep(10000);
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
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        String endpointId = UrlUtility.getId(tenantEndpoint);
        //delete client
        ResponseEntity<Void> client2 =
            ClientUtility.deleteTenantClient(tenantContext, randomClient);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
        Thread.sleep(10000);
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

    @Test
    public void validation_create_valid_backend() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    public void validation_create_valid_frontend() {
        Client client = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    public void validation_create_name_null() {
        Client client = ClientUtility.createValidBackendClient();
        //null
        client.setName(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_name_blank() {
        Client client = ClientUtility.createValidBackendClient();
        //blank
        client.setName(" ");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_name_empty() {
        Client client = ClientUtility.createValidBackendClient();
        //empty
        client.setName("");
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_name_invalid_char() {
        Client client = ClientUtility.createValidBackendClient();
        //invalid char
        client.setName("<");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_name_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        //max length
        client.setName(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_name_min_length() {
        Client client = ClientUtility.createValidBackendClient();
        //min length
        client.setName("01");
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_description_blank() {
        Client client = ClientUtility.createValidBackendClient();
        //blank
        client.setDescription(" ");
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_description_empty() {
        Client client = ClientUtility.createValidBackendClient();
        //empty
        client.setDescription("");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_description_invalid_char() {
        Client client = ClientUtility.createValidBackendClient();
        //invalid char
        client.setDescription("<");
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_description_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        //max length
        client.setDescription(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_secret_type_is_backend_and_secret_missing() {
        Client client = ClientUtility.createValidBackendClient();
        //type is backend and secret is missing
        client.setHasSecret(true);
        client.setClientSecret(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_secret_type_is_front_but_secret_present() {
        Client client = ClientUtility.createValidBackendClient();
        //type is frontend but secret is present
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        client.setClientSecret("test");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    public void validation_create_secret_secret_format() {
        Client client = ClientUtility.createValidBackendClient();

        //secret format
        client.setHasSecret(true);
        client.setClientSecret("0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response5.getStatusCode());
    }

    @Test
    public void validation_create_project_id_other_tenant_id() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //other tenant's id
        project.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url = ClientUtility.getUrl(project);
        ResponseEntity<Void> response2 =
            Utility.createResource(tenantContext.getCreator(), url, client);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response2.getStatusCode());
    }

    @Test
    public void validation_create_project_id_blank() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //blank
        project.setId(" ");
        String url2 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response3 =
            Utility.createResource(tenantContext.getCreator(), url2, client);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response3.getStatusCode());
    }

    @Test
    public void validation_create_project_id_empty() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //empty
        project.setId("");
        String url3 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response4 =
            Utility.createResource(tenantContext.getCreator(), url3, client);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response4.getStatusCode());
    }

    @Test
    public void validation_create_project_id_wrong_format() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //wrong format
        project.setId("abc");
        String url4 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response5 =
            Utility.createResource(tenantContext.getCreator(), url4, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_project_id_null() {
        Client client = ClientUtility.createValidBackendClient();
        Project project = new Project();
        //null
        project.setId("null");
        String url5 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response6 =
            Utility.createResource(tenantContext.getCreator(), url5, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_path_wrong_format_2() {
        Client client = ClientUtility.createValidBackendClient();

        //wrong path format
        client.setPath("/test/");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_path_wrong_path_format_1() {
        Client client = ClientUtility.createValidBackendClient();

        //wrong path format
        client.setPath("/test/");
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_path_wrong_type_format() {
        Client client = ClientUtility.createValidBackendClient();

        //wrong path format
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_create_path_type_is_backend_and_path_missing() {
        Client client = ClientUtility.createValidBackendClient();
        //type is backend and path is missing
        client.setPath(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_path_type_is_front_but_path_present() {
        Client client = ClientUtility.createValidBackendClient();
        Client client1 = ClientUtility.createValidFrontendClient();
        //type is frontend but path is present
        client1.setPath(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_path_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        //max length
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_path_min_length() {
        Client client = ClientUtility.createValidBackendClient();
        //min length
        client.setPath(RandomUtility.randomStringNoNum().substring(0, 4));
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_path_unique_across_application() {
        Client client = ClientUtility.createValidBackendClient();
        String repeatedPath = client.getPath();

        //unique across application
        client.setPath(repeatedPath);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response8.getStatusCode());
        ResponseEntity<Void> response11 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response11.getStatusCode());
    }

    @Test
    public void validation_create_external_url_wrong_format() {
        Client client1 = ClientUtility.createValidFrontendClient();

        //externalUrl format is wrong
        client1.setExternalUrl(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_external_url_type_is_backend_and_external_url_missing() {
        Client client = ClientUtility.createValidBackendClient();

        //type is backend and externalUrl is missing
        client.setExternalUrl(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_external_url_type_is_front_but_external_url_is_present() {

        Client client1 = ClientUtility.createValidFrontendClient();

        //type is frontend but externalUrl is present
        client1.setExternalUrl(RandomUtility.randomLocalHostUrl());
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_external_url_max_length() {
        Client client = ClientUtility.createValidBackendClient();

        //max length
        client.setExternalUrl(RandomUtility.randomLocalHostUrl() +
            "/abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_empty() {
        Client client = ClientUtility.createValidBackendClient();

        //grantType is empty
        client.setGrantTypeEnums(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_null() {
        Client client = ClientUtility.createValidBackendClient();

        //grantType is null
        client.setGrantTypeEnums(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_invalid_value() {
        Client client = ClientUtility.createValidBackendClient();

        //grantType invalid value
        client.setGrantTypeEnums(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_refresh_requires_password_first() {
        Client client = ClientUtility.createValidBackendClient();

        //refresh requires password first
        client.setGrantTypeEnums(Collections.singleton(GrantType.REFRESH_TOKEN.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_authorization_grant_but_registered_redirect_url_is_empty() {
        Client client1 = ClientUtility.createAuthorizationClientObj();

        //authorization grant but registered redirect uri is empty
        client1.setRegisteredRedirectUri(Collections.emptySet());
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_authorization_grant_but_registered_redirect_url_is_null() {
        Client client1 = ClientUtility.createAuthorizationClientObj();

        //authorization grant but registered redirect uri is null
        client1.setRegisteredRedirectUri(null);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_refresh_grant_but_refresh_token_0() {
        Client client = ClientUtility.createValidBackendClient();
        //refresh grant but refresh token is 0
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        client.setGrantTypeEnums(strings);
        client.setRefreshTokenValiditySeconds(0);
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_create_grant_type_refresh_grant_but_refresh_token_null() {
        Client client = ClientUtility.createValidBackendClient();
        //refresh grant but refresh token is null
        client.setRefreshTokenValiditySeconds(null);
        ResponseEntity<Void> response10 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
    }

    @Test
    public void validation_create_type_type_is_null() {
        Client client = ClientUtility.createValidBackendClient();
        //type is null
        client.setTypes(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_type_type_is_empty() {
        Client client = ClientUtility.createValidBackendClient();
        //type is empty
        client.setTypes(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_type_type_is_invalid() {
        Client client = ClientUtility.createValidBackendClient();
        //type is invalid
        client.setTypes(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_type_both_type_cannot_present() {
        Client client = ClientUtility.createValidBackendClient();
        //both type cannot present
        HashSet<String> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP.name());
        strings.add(ClientType.FRONTEND_APP.name());
        client.setTypes(strings);
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_access_token_validity_second_access_token_validity_seconds_is_null() {
        Client client = ClientUtility.createValidBackendClient();
        //accessTokenValiditySeconds is null
        client.setAccessTokenValiditySeconds(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_access_token_validity_second_access_token_validity_seconds_is_0() {
        Client client = ClientUtility.createValidBackendClient();
        //accessTokenValiditySeconds is 0
        client.setAccessTokenValiditySeconds(0);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_access_token_validity_second_value_too_large() {
        Client client = ClientUtility.createValidBackendClient();
        //value too large
        client.setAccessTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_access_token_validity_second_value_too_small() {
        Client client = ClientUtility.createValidBackendClient();
        //value too small
        client.setAccessTokenValiditySeconds(30);
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_registered_redirect_url_has_value_but_not_authorization_grant() {

        Client client = ClientUtility.createAuthorizationClientObj();
        //has value but not authorization grant
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_registered_redirect_url_wrong_format() {

        Client client = ClientUtility.createAuthorizationClientObj();
        //wrong format
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_registered_redirect_url_too_many_elements() {

        Client client = ClientUtility.createAuthorizationClientObj();
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_refresh_token_validity_second_has_value_but_not_password_grant() {
        Client client = ClientUtility.createValidBackendClient();

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //has value but not password grant
        client.setRefreshTokenValiditySeconds(120);
        grantTypes.remove(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_refresh_token_validity_second_value_too_large() {
        Client client = ClientUtility.createValidBackendClient();

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //value too large
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_refresh_token_validity_second_value_too_small() {
        Client client = ClientUtility.createValidBackendClient();

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //value too small
        client.setRefreshTokenValiditySeconds(1);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_resource_ids_too_many_elements() {
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

    }

    @Test
    public void validation_create_resource_ids_format() {
        Client client = ClientUtility.createValidBackendClient();
        //format
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(RandomUtility.randomStringNoNum());
        client.setResourceIds(strings2);
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());

    }

    @Test
    public void validation_create_resource_ids_resource_id_belong_to_other_project() {
        Client client = ClientUtility.createValidBackendClient();
        //resource id that belong to another project
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(AppConstant.CLIENT_ID_TEST_ID);
        client.setResourceIds(strings3);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());

    }

    @Test
    public void validation_create_resource_indicator_null() {
        Client client = ClientUtility.createValidBackendClient();

        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //null
        client.setResourceIndicator(null);
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_resource_indicator_true_but_is_frontend() {
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //true but is frontend
        client1.setResourceIndicator(true);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_auto_approve_missing_when_authorization_grant() {
        Client client = ClientUtility.createAuthorizationClientObj();
        //missing when authorization grant
        client.setAutoApprove(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_auto_approve_present_when_not_authorization_grant_and_redirect_url_missing() {
        Client client = ClientUtility.createAuthorizationClientObj();
        //present when not authorization grant and redirect url missing
        client.setAutoApprove(true);
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_auto_approve_present_when_not_authorization_grant() {
        Client client = ClientUtility.createAuthorizationClientObj();
        //present when not authorization grant
        client.setAutoApprove(true);
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomLocalHostUrl()));
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_project_id_other_tenant_id() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        //other tenant's id
        project.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url = ClientUtility.getUrl(project);
        ResponseEntity<Void> response2 =
            Utility.updateResource(tenantContext.getCreator(), url, client, client.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response2.getStatusCode());
    }

    @Test
    public void validation_update_project_id_blank() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        //blank
        project.setId(" ");
        String url2 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response3 =
            Utility.updateResource(tenantContext.getCreator(), url2, client, client.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response3.getStatusCode());
    }

    @Test
    public void validation_update_project_id_empty() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        //empty
        project.setId("");
        String url3 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response4 =
            Utility.updateResource(tenantContext.getCreator(), url3, client, client.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response4.getStatusCode());
    }

    @Test
    public void validation_update_project_id_wrong_format() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        //wrong format
        project.setId("abc");
        String url4 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response5 =
            Utility.updateResource(tenantContext.getCreator(), url4, client, client.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_project_id_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Project project = new Project();
        //null
        project.setId("null");
        String url5 = ClientUtility.getUrl(project);
        ResponseEntity<Void> response6 =
            Utility.updateResource(tenantContext.getCreator(), url5, client, client.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_secret_type_is_backend_and_secret_is_missing_then_secret_will_not_change() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));

        //type is backend and secret is missing, then secret will not change
        client.setHasSecret(true);
        client.setClientSecret(null);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

    @Test
    public void validation_update_secret_type_is_frontend_but_secret_is_present() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //type is frontend but secret is present
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        client1.setId(UrlUtility.getId(response2));
        client1.setClientSecret("test");
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    public void validation_update_secret_format() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //secret format
        client.setHasSecret(true);
        client.setClientSecret("0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response5.getStatusCode());
    }

    @Test
    public void validation_update_description_blank() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //blank
        client.setDescription(" ");
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_update_description_empty() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //empty
        client.setDescription("");
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_description_invalid_char() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //invalid char
        client.setDescription("<");
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_description_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //max length
        client.setDescription(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_path_wrong_path_format_1() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));

        //wrong path format
        client.setPath("/test/");
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_path_wrong_path_format_2() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        String repeatedPath = client.getPath();
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));
        //wrong path format
        client.setPath("/test-/");
        ResponseEntity<Void> response10 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
    }

    @Test
    public void validation_update_path_wrong_path_format_3() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));

        //wrong path format
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response9 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_update_path_type_is_backend_but_path_missing() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));

        //type is backend and path is missing
        client.setPath(null);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_path_type_is_front_but_path_present() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));

        //type is frontend but path is present
        client1.setPath(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_path_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));
        //max length
        client.setPath(RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response6 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_path_min_length() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));
        //min length
        client.setPath(RandomUtility.randomStringNoNum().substring(0, 4));
        ResponseEntity<Void> response7 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_update_external_url_wrong_format() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //externalUrl format is wrong
        client1.setExternalUrl(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_external_url_type_is_backend_and_external_url_is_missing() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //type is backend and externalUrl is missing
        client.setExternalUrl(null);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_external_url_type_is_frontend_but_external_url_present() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //type is frontend but externalUrl is present
        client1.setExternalUrl(RandomUtility.randomLocalHostUrl());
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_external_url_max_length() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //max length
        client1.setExternalUrl(RandomUtility.randomLocalHostUrl() +
            "/abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
        ResponseEntity<Void> response6 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //grantType is null
        client.setGrantTypeEnums(null);
        ResponseEntity<Void> response4 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_empty() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //grantType is empty
        client.setGrantTypeEnums(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_invalid_value() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //grantType invalid value
        client.setGrantTypeEnums(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response5 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_refresh_requires_password_first() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //refresh requires password first
        client.setGrantTypeEnums(Collections.singleton(GrantType.REFRESH_TOKEN.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_authorization_grant_but_registered_redirect_uri_is_empty() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //authorization grant but registered redirect uri is empty
        client1.setRegisteredRedirectUri(Collections.emptySet());
        ResponseEntity<Void> response7 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_authorization_grant_but_registered_redirect_uri_is_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //authorization grant but registered redirect uri is null
        client1.setRegisteredRedirectUri(null);
        ResponseEntity<Void> response8 =
            ClientUtility.createTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_refresh_grant_but_refresh_token_is_0() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //refresh grant but refresh token is 0
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        client.setGrantTypeEnums(strings);
        client.setRefreshTokenValiditySeconds(0);
        ResponseEntity<Void> response9 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_update_grant_type_refresh_grant_but_refresh_token_is_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        //refresh grant but refresh token is null
        client.setRefreshTokenValiditySeconds(null);
        ResponseEntity<Void> response10 =
            ClientUtility.createTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
    }

    @Test
    public void validation_update_access_token_validity_second_value_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //accessTokenValiditySeconds is null
        client.setAccessTokenValiditySeconds(null);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_update_access_token_validity_second_value_0() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //accessTokenValiditySeconds is 0
        client.setAccessTokenValiditySeconds(0);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_access_token_validity_second_value_too_large() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //value too large
        client.setAccessTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_access_token_validity_second_value_too_small() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //value too small
        client.setAccessTokenValiditySeconds(30);
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_registered_redirect_url_has_value_but_not_authorization_grant() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //has value but not authorization grant
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_registered_redirect_url_wrong_format() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //wrong format
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_registered_redirect_url_too_many_element() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
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
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_refresh_token_validity_second_has_value_but_not_password_grant() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response0));

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //has value but not password grant
        client.setRefreshTokenValiditySeconds(120);
        grantTypes.remove(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        ResponseEntity<Void> response2 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_update_refresh_token_validity_second_value_too_large() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response0));

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //value too large
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(Integer.MAX_VALUE);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_refresh_token_validity_second_value_too_small() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response0));

        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        client.setGrantTypeEnums(grantTypes);
        client.setRefreshTokenValiditySeconds(120);
        ResponseEntity<Void> response1 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        client.setPath(RandomUtility.randomStringNoNum());
        //value too small
        client.setRefreshTokenValiditySeconds(1);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_resource_ids_too_many_elements() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
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
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_update_resource_ids_format() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //format
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(RandomUtility.randomStringNoNum());
        client.setResourceIds(strings2);
        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_resource_ids_resource_id_that_belong_to_another_project() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //resource id that belong to another project
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(AppConstant.CLIENT_ID_TEST_ID);
        client.setResourceIds(strings3);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_resource_indicator_null() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));

        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //null
        client.setResourceIndicator(null);
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_resource_indicator_true_but_is_frontend() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));

        ResponseEntity<Void> response3 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //true but is frontend
        client1.setResourceIndicator(true);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_auto_approve_missing_when_authorization_grant() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //missing when authorization grant
        client.setAutoApprove(null);
        ResponseEntity<Void> response4 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_auto_approve_present_when_not_authorization_grant_and_redirect_url_missing() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //present when not authorization grant and redirect url missing
        client.setAutoApprove(true);
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response5 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_auto_approve_present_when_not_authorization_grant() {
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        //present when not authorization grant
        client.setAutoApprove(true);
        client.setRegisteredRedirectUri(Collections.singleton(RandomUtility.randomLocalHostUrl()));
        client.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.updateTenantClient(tenantContext, client);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_patch_description() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid char
        patchCommand.setValue("<");
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        patchCommand.setValue(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_patch_path() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        String repeatedPath = client.getPath();
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response0 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/path");
        //wrong path format
        patchCommand.setValue("/test/");
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong path format
        patchCommand.setValue("/test-/");
        ResponseEntity<Void> response10 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response10.getStatusCode());
        //wrong path format
        patchCommand.setValue(
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
                RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
                RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
                RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response9 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
        //type is backend and path is missing
        patchCommand.setValue(null);
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //type is frontend but path is present
        patchCommand.setValue(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client1, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //max length
        patchCommand.setValue(
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
                RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response6 =
            ClientUtility.patchTenantClient(tenantContext, client1, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //min length
        patchCommand.setValue(RandomUtility.randomStringNoNum().substring(0, 4));
        ResponseEntity<Void> response7 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
    }

    @Test
    public void validation_patch_grant_type() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/grantTypeEnums");
        //grantType is empty
        patchCommand.setValue(Collections.emptySet());
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //grantType is null
        patchCommand.setValue(null);
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //grantType invalid value
        patchCommand.setValue(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //refresh requires password first
        patchCommand.setValue(Collections.singleton(GrantType.REFRESH_TOKEN.name()));
        ResponseEntity<Void> response6 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //cannot set as refresh grant without refresh token sec detail
        HashSet<String> strings = new HashSet<>();
        strings.add(GrantType.PASSWORD.name());
        strings.add(GrantType.REFRESH_TOKEN.name());
        patchCommand.setValue(strings);
        ResponseEntity<Void> response9 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_patch_access_token_validity_second() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/accessTokenValiditySeconds");
        //accessTokenValiditySeconds is null
        patchCommand.setValue(null);
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //accessTokenValiditySeconds is 0
        patchCommand.setValue(0);
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //value too large
        patchCommand.setValue(Integer.MAX_VALUE);
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //value too small
        patchCommand.setValue(30);
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_patch_resource_ids() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/resourceIds");
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
        patchCommand.setValue(strings);
        ResponseEntity<Void> response2 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //format
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(RandomUtility.randomStringNoNum());
        patchCommand.setValue(strings2);
        ResponseEntity<Void> response3 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //resource id that belong to another project
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(AppConstant.CLIENT_ID_TEST_ID);
        patchCommand.setValue(strings3);
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());

    }

    @Test
    public void validation_patch_resource_indicator() {
        Client client = ClientUtility.createValidBackendClient();
        ResponseEntity<Void> response1 =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(response1));
        Client client1 = ClientUtility.createValidFrontendClient();
        ResponseEntity<Void> response2 =
            ClientUtility.createTenantClient(tenantContext, client1);
        client1.setId(UrlUtility.getId(response2));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/resourceIndicator");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> response5 =
            ClientUtility.patchTenantClient(tenantContext, client, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //true but is frontend
        patchCommand.setValue(true);
        ResponseEntity<Void> response4 =
            ClientUtility.patchTenantClient(tenantContext, client1, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }
}