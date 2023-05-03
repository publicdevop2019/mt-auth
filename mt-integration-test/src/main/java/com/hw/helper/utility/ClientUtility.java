package com.hw.helper.utility;

import static com.hw.helper.AppConstant.CLIENT_MGMT_URL;
import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;
import static com.hw.helper.AppConstant.TEST_REDIRECT_URL;

import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.GrantType;
import com.hw.helper.Project;
import com.hw.helper.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

public class ClientUtility {
    /**
     * get password grant client as non resource.
     *
     * @return client
     */
    public static Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(false);
        Set<ClientType> types = new HashSet<>();
        types.add(ClientType.BACKEND_APP);
        client.setTypes(types);
        return client;
    }

    public static Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(true);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP);
        client.setTypes(strings);
        return client;
    }

    /**
     * create password grant client with given resource ids.
     *
     * @param resourceIds resource client ids
     * @return client
     */
    public static Client getClientRaw(String... resourceIds) {
        Client client = new Client();
        client.setName(RandomUtility.randomStringWithNum());
        client.setClientSecret(RandomUtility.randomStringWithNum());
        client.setGrantTypeEnums(new HashSet<>(Collections.singletonList(GrantType.PASSWORD)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setResourceIds(new HashSet<>(Arrays.asList(resourceIds)));
        return client;
    }

    public static Client createRandomClientObj() {
        Client client = new Client();
        client.setName(RandomUtility.randomStringWithNum());
        client.setClientSecret(RandomUtility.randomStringWithNum());
        GrantType grantType = RandomUtility.randomEnum(GrantType.values());
        ClientType clientType = RandomUtility.randomEnum(ClientType.values());
        client.setGrantTypeEnums(Collections.singleton(grantType));
        client.setTypes(Collections.singleton(clientType));
        client.setAccessTokenValiditySeconds(RandomUtility.randomInt());
        client.setRefreshTokenValiditySeconds(RandomUtility.randomInt());
        client.setHasSecret(RandomUtility.randomBoolean());
        client.setPath(RandomUtility.randomStringNoNum());
        client.setExternalUrl(RandomUtility.randomLocalHostUrl());
        return client;
    }

    /**
     * create valid backend client with client_credential grant
     *
     * @return client
     */
    public static Client createRandomBackendClientObj() {
        Client randomClient = ClientUtility.createRandomClientObj();
        randomClient.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        randomClient.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS));
        randomClient.setAccessTokenValiditySeconds(180);
        randomClient.setRefreshTokenValiditySeconds(null);
        return randomClient;
    }

    public static Client createAuthorizationClientObj() {
        Client client = new Client();
        client.setName(RandomUtility.randomStringWithNum());
        client.setClientSecret(RandomUtility.randomStringWithNum());
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP));
        client.setGrantTypeEnums(
            new HashSet<>(Collections.singletonList(GrantType.AUTHORIZATION_CODE)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setRegisteredRedirectUri(Collections.singleton(TEST_REDIRECT_URL));
        client.setResourceIds(Collections.emptySet());
        return client;
    }

    public static ResponseEntity<Void> createClient(Client client) {
        String bearer =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(CLIENT_MGMT_URL, HttpMethod.POST, request, Void.class);
    }

    public static ResponseEntity<Void> createTenantClient(User user, Client client,
                                                          String projectId) {
        String bearer =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, projectId, "clients")),
                HttpMethod.POST, request, Void.class);
    }

    public static ResponseEntity<Void> deleteTenantClient(User user, Client client,
                                                          String projectId) {
        String bearer =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, projectId,
                        "clients/" + client.getId())),
                HttpMethod.DELETE, request, Void.class);
    }

    public static ResponseEntity<String> createClient(Client client, String changeId) {
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordAdmin =
            UserUtility.getJwtPasswordAdmin();
        String bearer = jwtPasswordAdmin.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        headers.set("changeId", changeId);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(CLIENT_MGMT_URL, HttpMethod.POST, request, String.class);
    }

    /**
     * create sso login client for tenant project
     *
     * @param tenantUser tenant user
     * @param project    project obj
     * @return new client id
     */
    public static String createTenantSsoLoginClient(User tenantUser, Project project) {
        //create sso login client
        Client client = createAuthorizationClientObj();
        ResponseEntity<Void> tenantClient =
            createTenantClient(tenantUser, client, project.getId());
        return tenantClient.getHeaders().getLocation().toString();
    }
}
