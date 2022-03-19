package com.hw.integration.identityaccess.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static com.hw.helper.UserAction.*;
import static com.hw.integration.identityaccess.oauth2.BIzUserTest.RESOURCE_OWNER;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class BizClientTest {

    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    UUID uuid;
    @Autowired
    private UserAction action;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void only_client_w_first_party_n_backend_role_can_be_create_as_resource() throws JsonProcessingException {
        Client client = action.getInvalidClientAsResource();
        ResponseEntity<String> exchange = action.createClient(client);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void create_non_resource_client_with_valid_resource_ids_then_able_to_use_this_client_to_login() throws JsonProcessingException {
        Client client = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> exchange = action.createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, exchange.getHeaders().getLocation().toString(), client.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void create_client_which_is_resource_itself_with_valid_resource_ids_then_able_to_use_this_client_to_login() throws JsonProcessingException {
        Client client = action.getClientAsResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> exchange = action.createClient(client);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, exchange.getHeaders().getLocation().toString(), client.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void should_not_able_to_create_client_which_is_resource_itself_with_wrong_resource_ids() throws JsonProcessingException {
        Client client = action.getClientAsResource(CLIENT_ID_TEST_ID);
        ResponseEntity<String> exchange = action.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void should_not_able_to_create_client_which_is_resource_itself_with_wrong_not_existing_resource_ids() throws JsonProcessingException {
        Client client = action.getClientAsNonResource(UUID.randomUUID().toString());
        ResponseEntity<String> exchange = action.createClient(client);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void root_account_can_read_client() {
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS;
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Client>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }

    @Test
    public void create_client_then_replace_it_with_different_client_only_password_is_empty_then_login_with_new_client_but_password_should_be_old_one() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        Client newClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        newClient.setClientSecret(" ");
        newClient.setVersion(0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, client1.getHeaders().getLocation().toString(), oldClient.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void create_client_then_replace_it_with_same_client_info_only_password_is_empty_N_times_then_client_version_should_not_increase() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        oldClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        ResponseEntity<Client> exchange1 = action.restTemplate.exchange(url, HttpMethod.GET, request, Client.class);

        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Assert.assertEquals(0, (int) exchange1.getBody().getVersion());
    }

    @Test
    public void create_client_then_update_it_tobe_resource() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        String clientSecret = oldClient.getClientSecret();
        oldClient.setResourceIndicator(true);
        oldClient.setClientSecret(" ");
        oldClient.setVersion(0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, client1.getHeaders().getLocation().toString(), clientSecret);

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());

    }

    @Test
    public void should_not_be_able_to_update_client_type_once_created() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        Client newClient = action.getInvalidClientAsResource(CLIENT_ID_RESOURCE_ID);
        newClient.setClientSecret(" ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s1 = mapper.writeValueAsString(newClient);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<Client> exchange2 = action.restTemplate.exchange(url, HttpMethod.GET, request, Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertTrue(exchange2.getBody().getTypes().contains(ClientType.BACKEND_APP));
        Assert.assertTrue(exchange2.getBody().getTypes().contains(ClientType.FIRST_PARTY));

    }

    @Test
    public void create_client_then_update_it_secret() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        Client newClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        newClient.setVersion(0);
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, client1.getHeaders().getLocation().toString(), newClient.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
        Assert.assertNotNull(tokenResponse1.getBody().getValue());
    }

    @Test
    public void delete_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = action.createClient(oldClient);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, client1.getHeaders().getLocation().toString(), oldClient.getClientSecret());

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse1.getStatusCode());
    }

    @Test
    public void root_client_is_not_deletable() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        Client oldClient = action.getClientAsNonResource(CLIENT_ID_RESOURCE_ID);
        /**
         * add ROLE_ROOT so it can not be deleted
         */
        oldClient.setTypes(new HashSet<>(Arrays.asList(ClientType.BACKEND_APP, ClientType.ROOT_APPLICATION)));
        ResponseEntity<String> client1 = action.createClient(oldClient);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + client1.getHeaders().getLocation().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, client1.getHeaders().getLocation().toString(), oldClient.getClientSecret());

        Assert.assertEquals(HttpStatus.OK, tokenResponse1.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_delete_resource_client() throws InterruptedException {
        Client clientAsResource = action.getClientAsResource();
        clientAsResource.setName("resource client");
        ResponseEntity<String> client = action.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource = action.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        clientAsNonResource.setName("non resource client");
        ResponseEntity<String> client1 = action.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ResourceOwner>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //delete resource client
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        String url4 = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + resourceClientId;
        HttpHeaders headers4 = new HttpHeaders();
        headers4.setBearerAuth(bearer);
        HttpEntity<String> request4 = new HttpEntity<>(null, headers4);
        ResponseEntity<String> exchange1 = action.restTemplate.exchange(url4, HttpMethod.DELETE, request4, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource should not have removed client
        String url5 = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + clientId;
        ResponseEntity<Client> exchange3 = action.restTemplate.exchange(url5, HttpMethod.GET, request4, Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Set<String> resourceIds = exchange3.getBody().getResourceIds();
        Assert.assertEquals(1, resourceIds.size());
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<ResourceOwner>> exchange2 = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", jwtPasswordWithClient.getBody().getRefreshToken().getValue());
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(params, headers2);
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = action.restTemplate.exchange(PROXY_URL_TOKEN, HttpMethod.POST, request2, DefaultOAuth2AccessToken.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<String> request5 = new HttpEntity<>(null, headers5);
        ResponseEntity<SumTotal<ResourceOwner>> exchange5 = action.restTemplate.exchange(url, HttpMethod.GET, request5, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

    @Test
    public void create_resource_client_and_client_which_access_it_then_resource_client_is_not_accessible() throws InterruptedException {
        Client clientAsResource = action.getClientAsResource();
        ResponseEntity<String> client = action.createClient(clientAsResource);
        Assert.assertEquals(HttpStatus.OK, client.getStatusCode());
        String resourceClientId = client.getHeaders().getLocation().toString();
        Client clientAsNonResource = action.getClientAsNonResource(resourceClientId, CLIENT_ID_OAUTH2_ID);
        HashSet<GrantTypeEnum> enums = new HashSet<>();
        enums.add(GrantTypeEnum.PASSWORD);
        enums.add(GrantTypeEnum.REFRESH_TOKEN);
        clientAsNonResource.setGrantTypeEnums(enums);
        clientAsNonResource.setRefreshTokenValiditySeconds(120);
        ResponseEntity<String> client1 = action.createClient(clientAsNonResource);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        String clientId = client1.getHeaders().getLocation().toString();
        String clientSecret = clientAsNonResource.getClientSecret();
        //get jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient.getStatusCode());
        // clientAsNonResource can access endpoint
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordWithClient.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ResourceOwner>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //update resource client to remove access
        clientAsResource.setResourceIndicator(false);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        String url4 = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS + "/" + resourceClientId;
        HttpHeaders headers4 = new HttpHeaders();
        headers4.setBearerAuth(bearer);
        HttpEntity<Client> request4 = new HttpEntity<>(clientAsResource, headers4);
        ResponseEntity<String> exchange1 = action.restTemplate.exchange(url4, HttpMethod.PUT, request4, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        Thread.sleep(10000);
        //clientAsNonResource can not access endpoint both access token
        ResponseEntity<SumTotal<ResourceOwner>> exchange2 = action.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange2.getStatusCode());
        //even refresh token will not work
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", jwtPasswordWithClient.getBody().getRefreshToken().getValue());
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBasicAuth(clientId, clientSecret);
        HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(params, headers2);
        ResponseEntity<DefaultOAuth2AccessToken> exchange4 = action.restTemplate.exchange(PROXY_URL_TOKEN, HttpMethod.POST, request2, DefaultOAuth2AccessToken.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, exchange4.getStatusCode());
        //get new jwt
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordWithClient3 = action.getJwtPasswordWithClient(clientId, clientSecret, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        Assert.assertEquals(HttpStatus.OK, jwtPasswordWithClient3.getStatusCode());
        // clientAsNonResource can access endpoint again
        HttpHeaders headers5 = new HttpHeaders();
        headers5.setBearerAuth(jwtPasswordWithClient3.getBody().getValue());
        HttpEntity<String> request5 = new HttpEntity<>(null, headers5);
        ResponseEntity<SumTotal<ResourceOwner>> exchange5 = action.restTemplate.exchange(url, HttpMethod.GET, request5, new ParameterizedTypeReference<>() {
        });
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
    }

}