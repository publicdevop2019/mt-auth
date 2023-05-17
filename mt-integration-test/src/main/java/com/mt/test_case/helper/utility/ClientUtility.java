package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.ClientType;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.AppConstant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
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
        client.setPath(RandomUtility.randomStringNoNum());
        client.setExternalUrl(RandomUtility.randomLocalHostUrl());
        return client;
    }

    public static Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(true);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP);
        client.setTypes(strings);
        client.setPath(RandomUtility.randomStringNoNum());
        client.setExternalUrl(RandomUtility.randomLocalHostUrl());
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
        randomClient.setPath(RandomUtility.randomStringNoNum());
        randomClient.setExternalUrl(RandomUtility.randomLocalHostUrl());
        return randomClient;
    }

    /**
     * create valid backend client with client_credential grant
     *
     * @return client
     */
    public static Client createRandomSharedBackendClientObj() {
        Client randomBackendClientObj = createRandomBackendClientObj();
        randomBackendClientObj.setResourceIndicator(true);
        return randomBackendClientObj;
    }

    /**
     * create sso login client
     *
     * @return client
     */
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
        client.setRegisteredRedirectUri(Collections.singleton(AppConstant.TEST_REDIRECT_URL));
        client.setResourceIds(Collections.emptySet());
        return client;
    }

    public static ResponseEntity<Void> createTenantClient(TenantUtility.TenantContext tenantContext,
                                                          Client client) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "clients")),
                HttpMethod.POST, request, Void.class);
    }

    public static ResponseEntity<Void> updateTenantClient(TenantUtility.TenantContext tenantContext,
                                                          Client client) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "clients/"+client.getId())),
                HttpMethod.PUT, request, Void.class);
    }

    public static ResponseEntity<Client> readTenantClient(TenantUtility.TenantContext tenantContext,
                                                          Client client) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "clients/" + client.getId())),
                HttpMethod.GET, request, Client.class);
    }
    public static ResponseEntity<SumTotal<Client>> readTenantClients(TenantUtility.TenantContext tenantContext) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "clients")),
                HttpMethod.GET, request, new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Void> deleteTenantClient(TenantUtility.TenantContext tenantContext, Client client) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
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
            .exchange(AppConstant.CLIENT_MGMT_URL, HttpMethod.POST, request, String.class);
    }

}
