package com.hw.helper.utility;

import com.hw.helper.Endpoint;
import com.hw.integration.single.access.tenant.TenantEndpointTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class EndpointUtility {
    public static ResponseEntity<String> createEndpoint(Endpoint endpoint) {
        String url = UrlUtility.getAccessUrl(TenantEndpointTest.ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(endpoint, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<String> expireEndpoint(String endpointId) {
        String url = UrlUtility.getAccessUrl(TenantEndpointTest.ENDPOINTS) + "/" + endpointId + "/expire";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"expireReason\":\"test\"}",headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }
}
