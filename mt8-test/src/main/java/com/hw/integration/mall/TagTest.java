package com.hw.integration.mall;

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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TagTest {
    @Autowired
    UserAction action;
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
    public void create_tag() {
        CreateTagCommand createTagCommand = new CreateTagCommand();
        createTagCommand.setName(action.getRandomStr());
        createTagCommand.setMethod(TagValueType.SELECT);
        createTagCommand.setType(TagType.GEN_ATTR);
        createTagCommand.setSelectValues(new HashSet<>(List.of("123", "234")));
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<CreateTagCommand> request = new HttpEntity<>(createTagCommand, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/attributes/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotSame("", exchange.getHeaders().getLocation().toString());
    }

    @Test
    public void create_tag_which_missing_select_value() {
        CreateTagCommand createTagCommand = new CreateTagCommand();
        createTagCommand.setName(action.getRandomStr());
        createTagCommand.setMethod(TagValueType.SELECT);
        createTagCommand.setType(TagType.GEN_ATTR);
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<CreateTagCommand> request = new HttpEntity<>(createTagCommand, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/attributes/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void manual_tag_should_not_have_select_value() {
        CreateTagCommand createTagCommand = new CreateTagCommand();
        createTagCommand.setName(action.getRandomStr());
        createTagCommand.setMethod(TagValueType.MANUAL);
        createTagCommand.setType(TagType.GEN_ATTR);
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<CreateTagCommand> request = new HttpEntity<>(createTagCommand, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/attributes/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        UpdateTagCommand updateTagCommand = new UpdateTagCommand();
        updateTagCommand.setName(action.getRandomStr());
        updateTagCommand.setMethod(TagValueType.MANUAL);
        updateTagCommand.setSelectValues(new HashSet<>(List.of("123", "234")));
        HttpEntity<UpdateTagCommand> request2 = new HttpEntity<>(updateTagCommand, headers);
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url +"/"+ exchange.getHeaders().getLocation().toString(), HttpMethod.PUT, request2, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());

    }
}
