package com.hw.integration.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserReactionTest {
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

    @Test
    public void add_report() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/reports";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
    @Test
    public void add_notInterested() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/notInterested";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
    @Test
    public void add_like() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/likes";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
    @Test
    public void add_like_then_dislike(){
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/likes";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        String url3 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
        ResponseEntity<String> exchange3 = action.restTemplate.exchange(url3, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }
    @Test
    public void add_dislike() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void delete_not_exist_dislike() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.DELETE, request, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void delete_dislike() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(s1);
        HttpEntity<String> request2 = new HttpEntity<>(null, headers2);
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url, HttpMethod.POST, request2, String.class);

        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.DELETE, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}
