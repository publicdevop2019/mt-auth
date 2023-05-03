package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.Endpoint;
import com.hw.helper.User;
import com.hw.integration.single.access.tenant.TenantEndpointTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class EndpointUtility {
    public static Endpoint createRandomEndpointObj(String clientId) {
        Endpoint endpoint = new Endpoint();
        endpoint.setResourceId(clientId);
        endpoint.setName(RandomUtility.randomStringWithNum());
        endpoint.setMethod(RandomUtility.randomHttpMethod());
        endpoint.setWebsocket(RandomUtility.randomBoolean());
        endpoint
            .setPath(
                "/test/" + RandomUtility.randomStringNoNum()
                    +
                    "/abc");
        return endpoint;
    }

    public static ResponseEntity<String> createEndpoint(Endpoint endpoint) {
        String url = UrlUtility.getAccessUrl(TenantEndpointTest.ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(endpoint, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<Void> createTenantEndpoint(User user, Endpoint endpoint,
                                                            String projectId) {
        String bearer =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Endpoint> request = new HttpEntity<>(endpoint, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, projectId, "endpoints")),
                HttpMethod.POST, request, Void.class);
    }

    public static ResponseEntity<String> expireEndpoint(String endpointId) {
        String url =
            UrlUtility.getAccessUrl(TenantEndpointTest.ENDPOINTS) + "/" + endpointId + "/expire";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity1 =
            new HttpEntity<>("{\"expireReason\":\"test\"}", headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<Endpoint> readTenantEndpoint(User user, String endpointId,
                                                              String projectId) {
        String bearer =
            UserUtility.login(user);
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, projectId, "endpoints/" + endpointId));
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, Endpoint.class);
    }
}
