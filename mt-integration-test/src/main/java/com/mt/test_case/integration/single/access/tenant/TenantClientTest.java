package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.ClientType;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.OAuth2Utility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import java.util.Collections;
import java.util.HashSet;
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
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP));
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
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD));
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
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setVersion(0);
        client.setGrantTypeEnums(Collections.singleton(GrantType.PASSWORD));
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
        Client client = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client1 = ClientUtility.createTenantClient(tenantContext, client);
        client.setClientSecret(" ");
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP));
        client.setId(UrlUtility.getId(client1));
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
        Client client = ClientUtility.createRandomBackendClientObj();
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
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
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
        HashSet<GrantType> enums = new HashSet<>();
        enums.add(GrantType.PASSWORD);
        enums.add(GrantType.REFRESH_TOKEN);
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
        Client randomClient = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomGetEndpointObj(clientId);
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
                tenantContext.getLoginClientId(), login, AppConstant.TEST_REDIRECT_URL);
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getOAuth2AuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, tenantContext.getLoginClientId(), "");
        Assert.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
    }

    @Test
    public void validation_create_name() {
        //null
        //blank
        //empty
        //invalid char
        //max length
        //min length
    }

    @Test
    public void validation_create_description() {
        //blank
        //invalid char
        //max length
    }

    @Test
    public void validation_create_has_secret() {
        //true but no secret
        //false but secret present
        //null but secret present

    }

    @Test
    public void validation_create_secret() {
        //type is backend and secret is missing
        //type is frontend but secret is present
        //secret format
    }

    @Test
    public void validation_create_project_id() {
        //other tenant's id
        //blank
        //empty
        //wrong format
        //null
    }

    @Test
    public void validation_create_path() {
        //wrong path format
        //type is backend and path is missing
        //type is frontend but path is present
        //max length
        //min length
        //unique across application
    }

    @Test
    public void validation_create_external_url() {
        //externalUrl format is wrong
        //type is backend and externalUrl is missing
        //type is frontend but externalUrl is present
        //max length
    }

    @Test
    public void validation_create_grant_type() {
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
    public void validation_create_type() {
        //type is null
        //type is empty
        //type is invalid
        //both type cannot present
    }

    @Test
    public void validation_create_access_token_validity_second() {
        //accessTokenValiditySeconds is null
        //accessTokenValiditySeconds is 0
        //value too large
        //value too small
    }


    @Test
    public void validation_create_registered_redirect_url() {
        //has value but not authorization grant
        //wrong format
        //too many elements
    }

    @Test
    public void validation_create_refresh_token_validity_second() {
        //has value but not password grant
        //value too large
        //value too small
    }

    @Test
    public void validation_create_resource_ids() {
        //too many elements
        //format
        //resource id that belong to another project

    }

    @Test
    public void validation_create_resource_indicator() {
        //null
        //true but is frontend
    }

    @Test
    public void validation_create_auto_approve() {
        //missing when authorization grant
        //present when not authorization grant
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