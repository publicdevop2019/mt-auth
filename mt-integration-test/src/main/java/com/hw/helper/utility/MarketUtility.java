package com.hw.helper.utility;

import static com.hw.helper.AppConstant.MARKET_ENDPOINT;
import static com.hw.helper.AppConstant.MARKET_ENDPOINT_SUB;

import com.hw.helper.Endpoint;
import com.hw.helper.SubscriptionReq;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class MarketUtility {
    public static ResponseEntity<SumTotal<Endpoint>> readMarketEndpoint(User user) {
        String bearer =
            UserUtility.login(user);
        String url = UrlUtility.getAccessUrl(MARKET_ENDPOINT);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<Void> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<SumTotal<Endpoint>> searchMarketEndpoint(User user,
                                                                          String endpointId) {
        String bearer =
            UserUtility.login(user);
        String url = UrlUtility.appendQuery(UrlUtility.getAccessUrl(MARKET_ENDPOINT),
            "query=id:" + endpointId);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<Void> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<Void> subToEndpoint(User user, SubscriptionReq req) {
        String bearer =
            UserUtility.login(user);
        String url = UrlUtility.getAccessUrl(MARKET_ENDPOINT_SUB);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 = new HttpEntity<>(req, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static SubscriptionReq createRandomTenantSubReqObj(
        TenantUtility.TenantContext tenantContext,
        String endpointId) {
        SubscriptionReq subscriptionReq = new SubscriptionReq();
        subscriptionReq.setProjectId(tenantContext.getProject().getId());
        subscriptionReq.setEndpointId(endpointId);
        subscriptionReq.setBurstCapacity(RandomUtility.randomInt());
        subscriptionReq.setReplenishRate(RandomUtility.randomInt());
        return subscriptionReq;
    }

    public static ResponseEntity<Void> approveSubReq(TenantUtility.TenantContext tenantContext,
                                                     String subReqId) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(MARKET_ENDPOINT_SUB, subReqId, "approve"));
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.POST, hashMapHttpEntity1, Void.class);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubReq(
        TenantUtility.TenantContext tenantContext) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String url = UrlUtility.appendQuery(UrlUtility.getAccessUrl(MARKET_ENDPOINT_SUB),
            "query=type:my_request");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMyPendingApprove(
        TenantUtility.TenantContext tenantContext) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String url = UrlUtility.appendQuery(UrlUtility.getAccessUrl(MARKET_ENDPOINT_SUB),
            "query=type:PENDING_APPROVAL");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubs(
        TenantUtility.TenantContext tenantContext) {
        String bearer =
            UserUtility.login(tenantContext.getCreator());
        String url = UrlUtility.getAccessUrl("subscriptions");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer);
        HttpEntity<SubscriptionReq> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }
}
