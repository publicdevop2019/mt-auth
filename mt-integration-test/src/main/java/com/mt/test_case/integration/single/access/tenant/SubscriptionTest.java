package com.mt.test_case.integration.single.access.tenant;

import static com.mt.test_case.integration.single.proxy.GatewayFilterTest.X_MT_RATELIMIT_LEFT;

import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Notification;
import com.mt.test_case.helper.pojo.SubscriptionReq;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.NotificationUtility;
import com.mt.test_case.helper.utility.SubReqUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.CommonTest;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
public class SubscriptionTest  extends CommonTest {

    //public api shared
    @Test
    public void external_shared_none_auth_api_has_rate_limit_on_ip_and_lifecycle_mgmt()
        throws InterruptedException {
        //check current notifications for later verify
        ResponseEntity<DefaultOAuth2AccessToken> login =
            UserUtility.getJwtPasswordMallTenant();
        ResponseEntity<SumTotal<Notification>> oldNotifications =
            NotificationUtility.getUserNotification(login.getBody().getValue());

        //mt-auth -> create public shared no auth endpoint
        Endpoint endpoint = new Endpoint();
        endpoint.setResourceId("0C8AZTODP4HZ");
        endpoint.setName("ExternalSharedNoneAuth");
        endpoint.setMethod("GET");
        endpoint.setWebsocket(false);
        endpoint.setExternal(true);
        endpoint.setShared(true);
        endpoint.setSecured(false);
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(20);
        endpoint
            .setPath(
                "test/expire/" + UUID.randomUUID().toString().replace("-", "").replaceAll("\\d", "")
                    +
                    "/random");
        ResponseEntity<String> endpoint1 = EndpointUtility.createEndpoint(endpoint);
        Assert.assertEquals(HttpStatus.OK, endpoint1.getStatusCode());
        String endpointId = endpoint1.getHeaders().getLocation().toString();
        Assert.assertNotNull(endpointId);
        Thread.sleep(20000);//wait for proxy update
        //mt-mall can subscribe to it
        SubscriptionReq subscriptionReq = new SubscriptionReq();
        subscriptionReq.setReplenishRate(10);
        subscriptionReq.setBurstCapacity(10);
        subscriptionReq.setProjectId("0P8HPG99R56P");
        subscriptionReq.setEndpointId(endpointId);

        ResponseEntity<String> subReq = SubReqUtility.createSubReqForMallProject(subscriptionReq);
        Assert.assertEquals(HttpStatus.OK, subReq.getStatusCode());

        String s = subReq.getHeaders().getLocation().toString();
        ResponseEntity<String> approveResult = SubReqUtility.approveSubReq(s);
        Assert.assertEquals(HttpStatus.OK, approveResult.getStatusCode());
        //rate limit should work
        String path = endpoint.getPath();
        //call new endpoint
        Thread.sleep(20000);//wait for proxy update
        String accessUrl = UrlUtility.getTestUrl(path);
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(jwtAdmin);
        HttpEntity<Void> entity =
            new HttpEntity<>(headers1);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.GET, entity, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String first = exchange.getHeaders().getFirst(X_MT_RATELIMIT_LEFT);
        Assert.assertEquals("19", first);
        //when api expire, notification is sent to mt-mall owner
        ResponseEntity<String> stringResponseEntity = EndpointUtility.expireEndpoint(endpointId);
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        Thread.sleep(20000);//wait for notification send
        ResponseEntity<DefaultOAuth2AccessToken> login2 =
            UserUtility.getJwtPasswordMallTenant();
        ResponseEntity<SumTotal<Notification>> newNotification =
            NotificationUtility.getUserNotification(login2.getBody().getValue());
        Assert.assertNotEquals(newNotification.getBody().getTotalItemCount(),
            oldNotifications.getBody().getTotalItemCount());
        List<Notification> data = newNotification.getBody().getData();
        Notification notification = data.get(data.size() - 1);
        Assert.assertEquals(notification.getTitle(), "SUBSCRIBER_ENDPOINT_EXPIRE");

    }

}
