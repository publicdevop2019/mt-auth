package com.hw.helper.utility;

import static com.hw.helper.AppConstant.CLIENT_MGMT_URL;

import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.GrantTypeEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

public class ClientUtility {
    /**
     * get client as non resource.
     *
     * @return different GRANT_TYPE_PASSWORD client obj
     */
    public static Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(false);
        Set<ClientType> types = new HashSet<>();
        types.add(ClientType.BACKEND_APP);
        types.add(ClientType.FIRST_PARTY);
        client.setTypes(types);
        return client;
    }

    public static Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(true);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP);
        strings.add(ClientType.FIRST_PARTY);
        client.setTypes(strings);
        return client;
    }

    public static Client getInvalidClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.FRONTEND_APP);
        strings.add(ClientType.FIRST_PARTY);
        client.setTypes(strings);
        client.setResourceIndicator(true);
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
        client.setName(UUID.randomUUID().toString().replace("-", ""));
        client.setClientSecret(UUID.randomUUID().toString().replace("-", ""));
        client.setGrantTypeEnums(new HashSet<>(Collections.singletonList(GrantTypeEnum.PASSWORD)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setResourceIds(new HashSet<>(Arrays.asList(resourceIds)));
        return client;
    }


    public static ResponseEntity<String> createClient(Client client) {
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordAdmin =
            UserUtility.getJwtPasswordAdmin();
        String bearer = jwtPasswordAdmin.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return TestContext.getRestTemplate()
            .exchange(CLIENT_MGMT_URL, HttpMethod.POST, request, String.class);
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
}
