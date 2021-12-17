package com.hw.integration.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
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

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class CartTest {
    public static final String CART = "/cart";
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Autowired
    UserAction action;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };
    @Autowired
    TestHelper helper;
    private final String ACCESS_ROLE_USER = "/root";

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void shop_add_product_cart_then_read_all() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        ResponseEntity<ProductDetailCustomRepresentation> exchange = action.readRandomProductDetail();
        SnapshotProduct snapshotProduct = action.selectProduct(exchange.getBody());
        String url2 = helper.getUserProfileUrl(CART + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange3 = action.restTemplate.exchange(url2, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, snapshotProduct), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Assert.assertNotEquals(-1, exchange3.getHeaders().getLocation().toString());
        String url = helper.getUserProfileUrl(CART + ACCESS_ROLE_USER);
        ResponseEntity<SumTotalSnapshotProduct> exchange2 = action.restTemplate.exchange(url, HttpMethod.GET, action.getHttpRequest(defaultUserToken), SumTotalSnapshotProduct.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertNotEquals(0, exchange2.getBody().getData().size());
    }

    @Test
    public void shop_add_same_product_cart_multiple_times() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        ResponseEntity<ProductDetailCustomRepresentation> exchange = action.readRandomProductDetail();
        SnapshotProduct snapshotProduct = action.selectProduct(exchange.getBody());
        String url2 = helper.getUserProfileUrl(CART + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange3 = action.restTemplate.exchange(url2, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, snapshotProduct), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Assert.assertNotEquals(-1, exchange3.getHeaders().getLocation().toString());
        ResponseEntity<String> exchange4 = action.restTemplate.exchange(url2, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, snapshotProduct), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
        Assert.assertNotEquals(-1, exchange4.getHeaders().getLocation().toString());
    }


    @Test
    public void shop_delete_cart_item_by_id() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        ResponseEntity<ProductDetailCustomRepresentation> exchange = action.readRandomProductDetail();
        SnapshotProduct snapshotProduct = action.selectProduct(exchange.getBody());
        String url2 = helper.getUserProfileUrl(CART + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange3 = action.restTemplate.exchange(url2, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, snapshotProduct), String.class);
        String s = exchange3.getHeaders().getLocation().toString();
        ResponseEntity<String> exchange4 = action.restTemplate.exchange(url2 + "/" + s, HttpMethod.DELETE, action.getHttpRequest(defaultUserToken), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
    }


}
