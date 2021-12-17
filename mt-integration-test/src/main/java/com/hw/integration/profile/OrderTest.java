package com.hw.integration.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.TestHelper;
import com.hw.helper.OrderDetail;
import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.SumTotalOrder;
import com.hw.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {
    public static final String ORDERS_USER = "/orders/user";
    @Autowired
    UserAction action;
    @Autowired
    TestHelper helper;
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);

    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void shop_create_an_order_but_not_confirm() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);
        String url3 = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
    }

    @Test
    public void shop_create_then_confirm_payment_for_an_order() throws InterruptedException {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);
        String url3 = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        Thread.sleep(5000);//wait for order create/update
        String url4 = helper.getUserProfileUrl(ORDERS_USER + "/" + getOrderId(exchange.getHeaders().getLocation().toString()));
        ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4 + "/confirm", HttpMethod.PUT, action.getHttpRequest(defaultUserToken), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
        Thread.sleep(5000);//wait for order create/update
        ResponseEntity<OrderDetail> exchange8 = action.restTemplate.exchange(url4, HttpMethod.GET, action.getHttpRequest(defaultUserToken), OrderDetail.class);
        Assert.assertEquals(HttpStatus.OK, exchange8.getStatusCode());
        Assert.assertEquals(true, exchange8.getBody().getPaid());
    }

    @Test
    public void shop_create_then_reserve_an_order() throws InterruptedException {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);
        String url3 = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        Thread.sleep(5000);//wait for order create/update
        String url4 = helper.getUserProfileUrl(ORDERS_USER + "/" + getOrderId(exchange.getHeaders().getLocation().toString()) + "/reserve");
        ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, action.getHttpRequestAsString(defaultUserToken, null), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
    }


    @Test
    public void shop_read_history_orders() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<SumTotalOrder> exchange = action.restTemplate.exchange(url, HttpMethod.GET, action.getHttpRequest(defaultUserToken), SumTotalOrder.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void shop_read_order_details() throws InterruptedException {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createOrderDetailForUser(defaultUserToken);
        String url3 = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Thread.sleep(5000);//wait for order create/update
        String url4 = helper.getUserProfileUrl(ORDERS_USER + "/" + getOrderId(exchange.getHeaders().getLocation().toString()));
        ResponseEntity<OrderDetail> exchange2 = action.restTemplate.exchange(url4, HttpMethod.GET, action.getHttpRequest(defaultUserToken), OrderDetail.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertNotNull(exchange2.getBody());


    }

    @Test
    public void shop_place_order_but_insufficient_actual_storage() throws InterruptedException {
        //create a product with 100 order storage & 0 actual storage
        ResponseEntity<String> exchange1 = action.createRandomProductDetail(0);
        // place an order for this product
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        OrderDetail orderDetailForUser = action.createBizOrderForUserAndProduct(defaultUserToken, exchange1.getHeaders().get("Location").get(0));
        String url3 = helper.getUserProfileUrl(ORDERS_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, orderDetailForUser), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        Thread.sleep(5000);//wait for order create/update
        String orderIdFromPaymentLink = getOrderId(exchange.getHeaders().getLocation().toString());
        String url4 = helper.getUserProfileUrl(ORDERS_USER + "/" + orderIdFromPaymentLink + "/confirm");
        ResponseEntity<String> exchange7 = action.restTemplate.exchange(url4, HttpMethod.PUT, action.getHttpRequest(defaultUserToken), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange7.getStatusCode());
    }

    public static String getOrderId(String link) {
        return link;
    }
}
