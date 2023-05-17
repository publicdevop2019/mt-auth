package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.SubscriptionReq;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

public class SubReqUtility {
    private static final String SUB_REQ_URL = "subscriptions/requests";

    public static ResponseEntity<String> createSubReqForMallProject(SubscriptionReq subscriptionReq) {
        String url = UrlUtility.getAccessUrl(SUB_REQ_URL);
        HttpHeaders headers1 = new HttpHeaders();
        ResponseEntity<DefaultOAuth2AccessToken> login =
            UserUtility.getJwtPasswordMallTenant();
        headers1.setBearerAuth(login.getBody().getValue());
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 =
            new HttpEntity<>(subscriptionReq, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    public static ResponseEntity<String> approveSubReq(String subReqId) {
        String url=UrlUtility.getAccessUrl("subscriptions/requests/"+subReqId+"/approve");
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(jwtAdmin);
        HttpEntity<Void> entity =
            new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, entity, String.class);
    }
}
