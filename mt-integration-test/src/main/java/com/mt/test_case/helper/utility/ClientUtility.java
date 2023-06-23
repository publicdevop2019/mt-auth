package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.ClientType;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
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
    private static final ParameterizedTypeReference<SumTotal<Client>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return UrlUtility.appendPath(TenantUtility.getTenantUrl(project), "clients");
    }

    /**
     * get password grant client as non resource.
     *
     * @return client
     */
    public static Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(false);
        Set<String> types = new HashSet<>();
        types.add(ClientType.BACKEND_APP.name());
        client.setTypes(types);
        client.setPath(RandomUtility.randomStringNoNum());
        client.setExternalUrl(RandomUtility.randomLocalHostUrl());
        return client;
    }

    public static Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(true);
        Set<String> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP.name());
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
        client.setGrantTypeEnums(
            new HashSet<>(Collections.singletonList(GrantType.PASSWORD.name())));
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
        client.setGrantTypeEnums(Collections.singleton(grantType.name()));
        client.setTypes(Collections.singleton(clientType.name()));
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
    public static Client createValidBackendClient() {
        Client randomClient = ClientUtility.createRandomClientObj();
        randomClient.setTypes(Collections.singleton(ClientType.BACKEND_APP.name()));
        randomClient.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        randomClient.setAccessTokenValiditySeconds(180);
        randomClient.setRefreshTokenValiditySeconds(null);
        randomClient.setPath(
            RandomUtility.randomStringNoNum() + "/" + RandomUtility.randomStringNoNum());
        randomClient.setExternalUrl(RandomUtility.randomLocalHostUrl());
        randomClient.setResourceIndicator(RandomUtility.randomBoolean());
        return randomClient;
    }

    /**
     * create valid frontend client with client_credential grant
     *
     * @return client
     */
    public static Client createValidFrontendClient() {
        Client randomClient = ClientUtility.createRandomClientObj();
        randomClient.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        randomClient.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()));
        randomClient.setAccessTokenValiditySeconds(180);
        randomClient.setRefreshTokenValiditySeconds(null);
        randomClient.setPath(null);
        randomClient.setExternalUrl(null);
        return randomClient;
    }

    /**
     * create valid backend client with client_credential grant
     *
     * @return client
     */
    public static Client createRandomSharedBackendClientObj() {
        Client randomBackendClientObj = createValidBackendClient();
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
        client.setTypes(Collections.singleton(ClientType.FRONTEND_APP.name()));
        client.setGrantTypeEnums(
            new HashSet<>(Collections.singletonList(GrantType.AUTHORIZATION_CODE.name())));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setRegisteredRedirectUri(Collections.singleton(AppConstant.TEST_REDIRECT_URL));
        client.setAutoApprove(Boolean.TRUE);
        client.setResourceIds(Collections.emptySet());
        client.setResourceIndicator(null);
        return client;
    }

    public static ResponseEntity<Void> createTenantClient(TenantContext tenantContext,
                                                          Client client) {
        String url = getUrl(tenantContext.getProject());
        return Utility.createResource(tenantContext.getCreator(), url, client);
    }

    public static ResponseEntity<Void> updateTenantClient(TenantContext tenantContext,
                                                          Client client) {
        String url = getUrl(tenantContext.getProject());
        return Utility.updateResource(tenantContext.getCreator(), url, client, client.getId());
    }

    public static ResponseEntity<Void> patchTenantClient(TenantContext tenantContext,
                                                         Client client, PatchCommand command) {
        String url = getUrl(tenantContext.getProject());
        return Utility.patchResource(tenantContext.getCreator(), url, command, client.getId());
    }

    public static ResponseEntity<Client> readTenantClient(TenantContext tenantContext,
                                                          Client client) {

        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, client.getId(), Client.class);
    }

    public static ResponseEntity<SumTotal<Client>> readTenantClients(TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Void> deleteTenantClient(TenantContext tenantContext,
                                                          Client client) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, client.getId());
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
