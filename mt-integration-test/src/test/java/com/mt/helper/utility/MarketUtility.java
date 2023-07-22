package com.mt.helper.utility;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.RejectSubRequestCommand;
import com.mt.helper.pojo.SubscriptionReq;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
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
        String url = UrlUtility.appendQuery(
            UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT),
            "query=id:" + endpointId);
        return Utility.readResource(user, url, reference);
    }

    public static ResponseEntity<Void> subToEndpoint(User user, SubscriptionReq req) {
        String url = UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB);
        return Utility.createResource(user, url, req);
    }

    public static SubscriptionReq createRandomTenantSubReqObj(TenantContext tenantContext,
                                                              String endpointId) {
        SubscriptionReq subscriptionReq = new SubscriptionReq();
        subscriptionReq.setProjectId(tenantContext.getProject().getId());
        subscriptionReq.setEndpointId(endpointId);
        subscriptionReq.setBurstCapacity(RandomUtility.randomInt());
        subscriptionReq.setReplenishRate(RandomUtility.randomInt());
        return subscriptionReq;
    }

    public static SubscriptionReq createValidSubReq(TenantContext tenantContext,
                                                    String endpointId) {
        SubscriptionReq req =
            createRandomTenantSubReqObj(tenantContext, endpointId);
        req.setBurstCapacity(20);
        req.setReplenishRate(10);
        return req;
    }

    public static ResponseEntity<Void> approveSubReq(TenantContext tenantContext,
                                                     String subReqId) {
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.MARKET_ENDPOINT_SUB, subReqId, "approve"));
        return Utility.createResource(tenantContext.getCreator(), url);
    }

    public static ResponseEntity<Void> rejectSubReq(TenantContext tenantContext,
                                                    String subReqId,
                                                    RejectSubRequestCommand command
    ) {
        String url = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.MARKET_ENDPOINT_SUB, subReqId, "reject"));
        return Utility.createResource(tenantContext.getCreator(), url, command);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubReq(
        TenantContext tenantContext) {
        String url =
            UrlUtility.appendQuery(
                UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB),
                "query=type:my_request");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMyPendingApprove(
        TenantContext tenantContext) {
        String url =
            UrlUtility.appendQuery(
                UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB),
                "query=type:PENDING_APPROVAL");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }

    public static ResponseEntity<SumTotal<SubscriptionReq>> viewMySubs(
        TenantContext tenantContext) {
        String url = UrlUtility.getAccessUrl("subscriptions");
        return Utility.readResource(tenantContext.getCreator(), url, reference2);
    }

    public static ResponseEntity<Void> updateSubReq(User user, SubscriptionReq req) {
        String url = UrlUtility.getAccessUrl(AppConstant.MARKET_ENDPOINT_SUB);
        return Utility.updateResource(user, url, req, req.getId());
    }
}
