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
public class AddressTest {
    public static final String ADDRESSES = "/addresses";
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Autowired
    UserAction action;
    @Autowired
    TestHelper helper;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };
    private final String ACCESS_ROLE_USER = "/root";

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void shop_read_all_addresses() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        ResponseEntity<SumTotalAddress> exchange = action.restTemplate.exchange(url, HttpMethod.GET, action.getHttpRequest(defaultUserToken), SumTotalAddress.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(-1, exchange.getBody().getData().size());
    }

    @Test
    public void shop_read_address_details() {

        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, action.getRandomAddress()), String.class);
        String addressId = exchange.getHeaders().getLocation().toString();
        String url2 = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER + "/" + addressId);
        ResponseEntity<Address> exchange2 = action.restTemplate.exchange(url2, HttpMethod.GET, action.getHttpRequest(defaultUserToken), Address.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

    }

    @Test
    public void shop_update_address_details() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, action.getRandomAddress()), String.class);
        String s = exchange.getHeaders().getLocation().toString();
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url + "/" + s, HttpMethod.PUT, action.getHttpRequestAsString(defaultUserToken, action.getRandomAddress()), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void shop_create_address() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, action.getRandomAddress()), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
    }

    @Test
    public void should_not_able_to_create_same_address() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        Address randomAddress = action.getRandomAddress();
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, randomAddress), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getHeaders().getLocation().toString());
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, randomAddress), String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
    }

    @Test
    public void shop_delete_address() {
        String defaultUserToken = action.registerResourceOwnerThenLogin();
        String url = helper.getUserProfileUrl(ADDRESSES + ACCESS_ROLE_USER);
        Address randomAddress = action.getRandomAddress();
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, action.getHttpRequestAsString(defaultUserToken, randomAddress), String.class);
        String s = exchange.getHeaders().getLocation().toString();
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url + "/" + s, HttpMethod.DELETE, action.getHttpRequestAsString(defaultUserToken, randomAddress), String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }
}
