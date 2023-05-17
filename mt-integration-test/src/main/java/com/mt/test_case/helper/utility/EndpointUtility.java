package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.integration.single.access.tenant.TenantEndpointTest;
import com.mt.test_case.helper.AppConstant;
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

    /**
     * create valid random GET endpoint
     *
     * @param clientId client id
     * @return created endpoint
     */
    public static Endpoint createRandomGetEndpointObj(String clientId) {
        Endpoint randomEndpointObj = EndpointUtility.createRandomEndpointObj(clientId);
        randomEndpointObj.setWebsocket(false);
        randomEndpointObj.setMethod("GET");
        return randomEndpointObj;
    }

    /**
     * create valid random shared endpoint
     * note client must be accessible too
     *
     * @param clientId client id
     * @return created endpoint
     */
    public static Endpoint createRandomSharedEndpointObj(String clientId) {
        Endpoint randomEndpointObj = EndpointUtility.createRandomEndpointObj(clientId);
        randomEndpointObj.setShared(true);
        randomEndpointObj.setExternal(true);
        randomEndpointObj.setSecured(true);
        return randomEndpointObj;
    }

    /**
     * create valid random public endpoint
     *
     * @param clientId client id
     * @return created endpoint
     */
    public static Endpoint createRandomPublicEndpointObj(String clientId) {
        Endpoint randomEndpointObj = EndpointUtility.createRandomEndpointObj(clientId);
        randomEndpointObj.setSecured(false);
        randomEndpointObj.setExternal(true);
        return randomEndpointObj;
    }

    public static ResponseEntity<String> createEndpoint(Endpoint endpoint) {
        String url = UrlUtility.getAccessUrl(TenantEndpointTest.ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(endpoint, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<Void> createTenantEndpoint(
        TenantUtility.TenantContext tenantContext, Endpoint endpoint) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Endpoint> request = new HttpEntity<>(endpoint, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "endpoints")),
                HttpMethod.POST, request, Void.class);
    }

    public static ResponseEntity<Void> updateTenantEndpoint(User user, Endpoint endpoint,
                                                            String projectId) {
        String bearer =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Endpoint> request = new HttpEntity<>(endpoint, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, projectId,
                        "endpoints/" + endpoint.getId())),
                HttpMethod.PUT, request, Void.class);
    }

    public static ResponseEntity<Void> deleteTenantEndpoint(User user, Endpoint endpoint,
                                                            String projectId) {
        String bearer =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Endpoint> request = new HttpEntity<>(endpoint, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, projectId,
                        "endpoints/" + endpoint.getId())),
                HttpMethod.DELETE, request, Void.class);
    }

    public static ResponseEntity<Void> expireTenantEndpoint(
        TenantUtility.TenantContext tenantContext, Endpoint endpoint) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String accessUrl = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                "endpoints/" + endpoint.getId() + "/expire"));
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        headers1.setContentType(MediaType.APPLICATION_JSON);
        String reason = RandomUtility.randomStringWithNum();
        HttpEntity<String> hashMapHttpEntity1 =
            new HttpEntity<>("{\"expireReason\":\"" + reason + "\"}", headers1);
        return TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.POST, hashMapHttpEntity1, Void.class);
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

    public static ResponseEntity<Endpoint> readTenantEndpoint(
        TenantUtility.TenantContext tenantContext, Endpoint endpoint) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                "endpoints/" + endpoint.getId()));
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, Endpoint.class);
    }
}
