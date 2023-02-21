package com.hw.integration.identityaccess.proxy;

import com.hw.helper.EndpointInfo;
import com.hw.helper.Notification;
import com.hw.helper.SubscriptionReq;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.NotificationUtility;
import com.hw.helper.utility.SubReqUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
public class SubscriptionTest {
    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    @Ignore//TODO
    public void proxy_should_reject_any_call_to_internal_api_from_external_network() {

    }

    //public api shared
    @Test
    public void external_shared_none_auth_api_has_rate_limit_on_ip_and_lifecycle_mngmt()
        throws InterruptedException {
        //check current notifications for later verify
        ResponseEntity<DefaultOAuth2AccessToken> login =
            UserUtility.getJwtPasswordMallTenant();
        ResponseEntity<SumTotal<Notification>> oldNotifications =
            NotificationUtility.getUserNotification(login.getBody().getValue());

        //mt-auth -> create public shared no auth endpoint
        EndpointInfo endpoint = new EndpointInfo();
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
        String first = exchange.getHeaders().getFirst("x-mt-ratelimit-left");
        Assert.assertEquals("19", first);
        //when api expire, notification is send to mt-mall owner
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

    @Test
    @Ignore//TODO
    public void external_shared_auth_api_has_rate_limit_on_user_id_and_lifecycle_mngmt() {

    }

    //public api none-shared
    @Test
    @Ignore//TODO
    public void external_none_shared_none_auth_api_has_rate_limit_on_ip_without_lifecycle_mngmt() {

    }

    @Test
    @Ignore//TODO
    public void external_none_shared_auth_api_has_rate_limit_on_user_id_without_lifecycle_mngmt() {

    }
}
