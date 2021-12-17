package com.hw.chaos;

import com.hw.helper.*;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
/**
 * create clean env before test
 * UPDATE product_detail SET order_storage = 1000;
 * UPDATE product_detail SET actual_storage = 500;
 * UPDATE product_detail SET sales = NULL ;
 * DELETE FROM change_record ;
 */
@Component
public class ChaosTest {
    @Autowired
    UserAction action;
    @Autowired
    TestHelper helper;

    UUID uuid;

    private void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    /**
     * concurrent_create_order
     * randomly_pay
     * randomly_replace
     * after some time validate order storage actually storage
     */
    public void testCase1() {
        int numOfConcurrent = 5;
        setUp();
        Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(5000);//give some delay
                // randomly pick test user
                log.info("thread start");
                ResourceOwner user = action.testUser.get(new Random().nextInt(action.testUser.size()));
                String userAuthToken = action.getJwtPassword(user.getEmail(), user.getPassword()).getBody().getValue();
                OrderDetail orderDetail1 = action.createOrderDetailForUser(userAuthToken);
                log.info("draft order generated");
                String url3 = helper.getUserProfileUrl("/orders/user");
                action.restTemplate.exchange(url3, HttpMethod.POST, action.getHttpRequestAsString(userAuthToken, orderDetail1), String.class);
                Thread.sleep(15*1000);//wait for order creation
                String getAllOrderUrl = helper.getUserProfileUrl("/orders/user");
                ResponseEntity<SumTotalOrder> allOrders = action.restTemplate.exchange(getAllOrderUrl, HttpMethod.GET, action.getHttpRequest(userAuthToken), SumTotalOrder.class);
                if (allOrders.getBody() != null && allOrders.getBody().getData().size() > 0) {
                    int randomValue = new Random().nextInt(30);
                    OrderDetail randomOrder = allOrders.getBody().getData().get(new Random().nextInt(allOrders.getBody().getData().size()));
                    if (randomValue < 5) {
                        log.info("randomly pay");
                        String payOrderUrl = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId() + "/confirm");
                        action.restTemplate.exchange(payOrderUrl, HttpMethod.PUT, action.getHttpRequest(userAuthToken), String.class);
                    } else if (randomValue < 10) {
                        log.info("randomly reserve");
                        String url5 = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId() + "/reserve");
                        action.restTemplate.exchange(url5, HttpMethod.PUT, action.getHttpRequestAsString(userAuthToken, null), String.class);
                    } else if (randomValue < 15) {
                        log.info("reserve then directly pay");
                        String url5 = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId() + "/reserve");
                        action.restTemplate.exchange(url5, HttpMethod.PUT, action.getHttpRequestAsString(userAuthToken, null), String.class);
                        // after replace, directly pay
                        String payOrderUrl = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId() + "/confirm");
                        action.restTemplate.exchange(payOrderUrl, HttpMethod.PUT, action.getHttpRequest(userAuthToken), String.class);
                    } else if (randomValue < 20) {
                        log.info("randomly delete");
                        String url6 = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId());
                        action.restTemplate.exchange(url6, HttpMethod.DELETE, action.getHttpRequest(userAuthToken), String.class);
                    } else if (randomValue < 25) {
                        log.info("randomly update address");
                        String url5 = helper.getUserProfileUrl("/orders/user/" + randomOrder.getId());
                        Address address = new Address();
                        address.setCountry("testCountry");
                        address.setProvince("testProvince");
                        address.setCity("testCity");
                        address.setLine1("testLine1");
                        address.setLine2("testLine2");
                        address.setPostalCode("testPostalCode");
                        address.setPhoneNumber("testPhoneNumber");
                        address.setFullName("testFullName");
                        action.restTemplate.exchange(url5, HttpMethod.PUT, action.getHttpRequestAsString(userAuthToken, address), String.class);
                    } else {
                        log.info("do nothing");
                    }
                }
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, numOfConcurrent).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            UserAction.assertConcurrent("", runnables, 300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
