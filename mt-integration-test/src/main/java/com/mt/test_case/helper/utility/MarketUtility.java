package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.SubscriptionReq;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class MarketUtility {

    private static final ParameterizedTypeReference<SumTotal<Endpoint>> reference =
        new ParameterizedTypeReference<>() {
        };
    private static final ParameterizedTypeReference<SumTotal<SubscriptionReq>> reference2 =
        new ParameterizedTypeReference<>() {
        };

    public static ResponseEntity<SumTotal<Endpoint>> readMarketEndpoint(User user) {
        String url = UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT);
        return Utility.readResource(user, url, reference);
    }

    public static ResponseEntity<SumTotal<Endpoint>> searchMarketEndpoint(User user,
                                                                          String endpointId) {
        String url = UrlUtility.appendQuery(UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT),
            "query=id:" + endpointId);
        return Utility.readResource(user, url, reference);
    }

    public static ResponseEntity<Void> subToEndpoint(User user, SubscriptionReq req) {
        String url = UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB);
        return Utility.createResource(user, url, req);
    }

    public static SubscriptionReq createRandomTenantSubReqObj(
        TenantContext tenantContext,
        String endpointId) {
        SubscriptionReq subscriptionReq = new SubscriptionReq();
        subscriptionReq.setProjectId(tenantContext.getProject().getId());
        subscriptionReq.setEndpointId(endpointId);
        subscriptionReq.setBurstCapacity(RandomUtility.randomInt());
        subscriptionReq.setReplenishRate(RandomUtility.randomInt());
        return subscriptionReq;
    }

    public static ResponseEntity<Void> approveSubReq(TenantContext tenantContext,
                                                     String subReqId) {
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.MARKET_ENDPOINT_SUB, subReqId, "approve"));
        return Utility.createResource(tenantContext.getCreator(), url);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubReq(
        TenantContext tenantContext) {
        String url =
            UrlUtility.appendQuery(UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB),
                "query=type:my_request");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMyPendingApprove(
        TenantContext tenantContext) {
        String url =
            UrlUtility.appendQuery(UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB),
                "query=type:PENDING_APPROVAL");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubs(
        TenantContext tenantContext) {
        String url = UrlUtility.getAccessUrl("subscriptions");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }
}
