package com.mt.integration.single.access.tenant;

import static com.mt.helper.AppConstant.X_MT_RATELIMIT_LEFT;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Notification;
import com.mt.helper.pojo.SubscriptionReq;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.MarketUtility;
import com.mt.helper.utility.MessageUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Slf4j
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
public class SubscriptionTest{
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    //public api shared
    @Test
    public void external_shared_none_auth_api_has_rate_limit_on_ip_and_lifecycle_mgmt()
        throws InterruptedException {
        //check current notifications for later verify
        User user = new User();
        user.setEmail(AppConstant.ACCOUNT_USERNAME_MALL_ADMIN);
        user.setPassword(AppConstant.ACCOUNT_PASSWORD_MALL_ADMIN);
        User adminUser = new User();
        adminUser.setEmail(AppConstant.ACCOUNT_USERNAME_ADMIN);
        adminUser.setPassword(AppConstant.ACCOUNT_PASSWORD_ADMIN);
        ResponseEntity<SumTotal<Notification>> oldNotifications =
            MessageUtility.readMessages(user);
        //mt-auth -> create public shared no auth endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj("0C8AZTODP4HZ");
        endpoint.setSecured(false);
        endpoint.setPath("test/expire/" + RandomUtility.randomStringNoNum() + "/random");
        endpoint.setMethod("GET");
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(20);
        ResponseEntity<String> endpoint1 = EndpointUtility.createEndpoint(endpoint);
        Assertions.assertEquals(HttpStatus.OK, endpoint1.getStatusCode());
        String endpointId = HttpUtility.getId(endpoint1);
        Assertions.assertNotNull(endpointId);
        Thread.sleep(10*1000);//wait for proxy update
        //mt-mall can subscribe to it
        SubscriptionReq subscriptionReq = new SubscriptionReq();
        subscriptionReq.setReplenishRate(10);
        subscriptionReq.setBurstCapacity(10);
        subscriptionReq.setProjectId("0P8HPG99R56P");
        subscriptionReq.setEndpointId(endpointId);

        ResponseEntity<Void> subReq =
            MarketUtility.subToEndpoint(user, subscriptionReq);
        Assertions.assertEquals(HttpStatus.OK, subReq.getStatusCode());

        TenantContext tenantContext = new TenantContext();
        tenantContext.setCreator(adminUser);
        ResponseEntity<Void> approveResult =
            MarketUtility.approveSubReq(tenantContext, HttpUtility.getId(subReq));
        Assertions.assertEquals(HttpStatus.OK, approveResult.getStatusCode());
        //rate limit should work
        String path = endpoint.getPath();
        //call new endpoint
        Thread.sleep(10*1000);//wait for proxy update
        String accessUrl = HttpUtility.getTestUrl(path);
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(jwtAdmin);
        HttpEntity<Void> entity =
            new HttpEntity<>(headers1);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String first = exchange.getHeaders().getFirst(X_MT_RATELIMIT_LEFT);
        Assertions.assertEquals("19", first);
        //when api expire, notification is sent to mt-mall owner
        ResponseEntity<String> stringResponseEntity = EndpointUtility.expireEndpoint(endpointId);
        Assertions.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        Thread.sleep(5*1000);//wait for notification send
        ResponseEntity<SumTotal<Notification>> newNotification =
            MessageUtility.readMessages(user);
        Assertions.assertNotEquals(newNotification.getBody().getTotalItemCount(),
            oldNotifications.getBody().getTotalItemCount());
        List<Notification> data = newNotification.getBody().getData();
        Notification notification = data.get(data.size() - 1);
        Assertions.assertEquals(notification.getTitle(), "SUBSCRIBER_ENDPOINT_EXPIRE");

    }

}
