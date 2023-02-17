package com.hw.helper.utility;

import com.hw.helper.EndpointInfo;
import com.hw.integration.identityaccess.oauth2.EndpointTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class EndpointUtility {
    public static ResponseEntity<String> createEndpoint(EndpointInfo endpointInfo) {
        String url = UrlUtility.getAccessUrl(EndpointTest.ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<EndpointInfo> hashMapHttpEntity1 = new HttpEntity<>(endpointInfo, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<String> expireEndpoint(String endpointId) {
        String url = UrlUtility.getAccessUrl(EndpointTest.ENDPOINTS) + "/" + endpointId + "/expire";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"expireReason\":\"test\"}",headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }
}
