package com.hw.concurrent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.TestHelper;
import com.hw.helper.*;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.hw.helper.UserAction.*;
import static com.hw.integration.profile.OrderTest.getOrderId;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Autowired
    UserAction action;
    @Autowired
    TestHelper helper;
    int numOfConcurrent = 1;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void place_then_pay_an_order_current() {
        int numOfConcurrent = 10;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String defaultUserToken = action.registerResourceOwnerThenLogin();
                OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);

                String url3 = helper.getUserProfileUrl("/orders/user");
                ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
                Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
                Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
                String url4 = helper.getUserProfileUrl("/orders/user/" + getOrderId(exchange.getHeaders().getLocation().toString()) + "/confirm");
                ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, action.getHttpRequest(defaultUserToken), String.class);
                Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
                log.info("payment result {}", exchange7.getBody());
                Boolean read = JsonPath.read(exchange7.getBody(), "$.paymentStatus");
                Assert.assertEquals(true, read);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, numOfConcurrent).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void place_an_order_then_update_its_status() throws InterruptedException {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);

        String url3 = helper.getUserProfileUrl("/orders/user");
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        String orderIdFromPaymentLink = getOrderId(exchange.getHeaders().getLocation().toString());
        ResponseEntity<DefaultOAuth2AccessToken> jwtClientCredential = action.getJwtClientCredential(CLIENT_ID_SAGA_ID, COMMON_CLIENT_SECRET);
        String sagaToken = jwtClientCredential.getBody().getValue();
        UpdateBizOrderCommand updateBizOrderCommand = new UpdateBizOrderCommand();
        updateBizOrderCommand.setOrderId(orderIdFromPaymentLink);
        updateBizOrderCommand.setVersion(0);
        updateBizOrderCommand.setOrderState(BizOrderStatus.NOT_PAID_RECYCLED.name());
        updateBizOrderCommand.setChangeId(UUID.randomUUID().toString());
        String url4 = helper.getUserProfileUrl("/orders/app/" + orderIdFromPaymentLink);
        String url5 = helper.getUserProfileUrl("/orders/user/" + orderIdFromPaymentLink);
        HttpEntity<UpdateBizOrderCommand> httpRequest = action.getHttpRequest(sagaToken, updateBizOrderCommand);
        //update order for the first time, this is fine
        ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, httpRequest, String.class);
        Thread.sleep(5000);
        Assert.assertTrue(exchange7.getStatusCode().is2xxSuccessful());
        ResponseEntity<OrderDetail> exchange10 = action.restTemplate.exchange(url5, HttpMethod.GET, httpRequest, OrderDetail.class);
        Assert.assertEquals(BizOrderStatus.NOT_PAID_RECYCLED.name(), exchange10.getBody().getOrderState());
        //try to update with old version which is not right
        updateBizOrderCommand.setOrderState(BizOrderStatus.NOT_PAID_RESERVED.name());
        ResponseEntity<String> exchange8 = action.restTemplate.exchange(url4, HttpMethod.PUT, httpRequest, String.class);
        Assert.assertTrue(exchange8.getStatusCode().is4xxClientError());
        ResponseEntity<OrderDetail> exchange9 = action.restTemplate.exchange(url5, HttpMethod.GET, httpRequest, OrderDetail.class);
        Assert.assertEquals(BizOrderStatus.NOT_PAID_RECYCLED.name(), exchange9.getBody().getOrderState());

    }

    @Test
    public void place_an_order_then_update_its_status_concurrent() {
        int numOfConcurrent = 10;
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);

        String url3 = helper.getUserProfileUrl("/orders/user");
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        String orderIdFromPaymentLink = getOrderId(exchange.getHeaders().getLocation().toString());
        ResponseEntity<DefaultOAuth2AccessToken> jwtClientCredential = action.getJwtClientCredential(CLIENT_ID_SAGA_ID, COMMON_CLIENT_SECRET);
        String value = jwtClientCredential.getBody().getValue();
        UpdateBizOrderCommand updateBizOrderCommand = new UpdateBizOrderCommand();
        updateBizOrderCommand.setOrderId(orderIdFromPaymentLink);
        updateBizOrderCommand.setVersion(0);
        updateBizOrderCommand.setOrderState(BizOrderStatus.NOT_PAID_RECYCLED.name());
        updateBizOrderCommand.setChangeId(UUID.randomUUID().toString());
        HttpEntity<UpdateBizOrderCommand> httpRequest = action.getHttpRequest(value, updateBizOrderCommand);
        //concurrent update
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateBizOrderCommand.setChangeId(UUID.randomUUID().toString());
                String url4 = helper.getUserProfileUrl("/orders/app/" + orderIdFromPaymentLink);
                HttpEntity<UpdateBizOrderCommand> httpRequest = action.getHttpRequest(value, updateBizOrderCommand);

                ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, httpRequest, String.class);
                if (exchange7.getStatusCode().is2xxSuccessful()) {
                    success.getAndIncrement();
                } else if (exchange7.getStatusCode().is4xxClientError()) {
                    fail.getAndIncrement();
                } else if (exchange7.getStatusCode().is5xxServerError()) {
                    fail.getAndIncrement();
                }
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, 10).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
            Assert.assertEquals(numOfConcurrent - 1, fail.get());
            Assert.assertEquals(1, success.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * this test need to send reserve request right after payment confirmed
     * and autoConfirm is not finished
     *
     * @assert need to search for transaction_task see if there's task in
     * started status, expected is zero task found
     */
    @Test
    public void place_order_then_confirm_pay_and_reserve_at_same_time() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);
        String url3 = UserAction.proxyUrl + UserAction.SVC_NAME_PROFILE + "/orders/user";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        String orderIdFromPaymentLink = getOrderId(exchange.getHeaders().getLocation().toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url4 = UserAction.proxyUrl + UserAction.SVC_NAME_PROFILE + "/orders/user/" + orderIdFromPaymentLink + "/confirm";
                ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, action.getHttpRequest(defaultUserToken), String.class);
                Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
                Boolean read = JsonPath.read(exchange7.getBody(), "$.paymentStatus");
                Assert.assertEquals(true, read);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SnapshotAddress snapshotAddress = new SnapshotAddress();
                BeanUtils.copyProperties(action.getRandomAddress(), snapshotAddress);
                orderDetailForUser.setAddress(snapshotAddress);

                String url4 = UserAction.proxyUrl + UserAction.SVC_NAME_PROFILE + "/orders/user/" + orderIdFromPaymentLink + "/reserve";
                ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
                Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable);
        runnables.add(runnable2);
        try {
            assertConcurrent("", runnables, 30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
