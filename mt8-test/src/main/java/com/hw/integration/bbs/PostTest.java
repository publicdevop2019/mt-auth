package com.hw.integration.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostTest {
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
    public void create_post() throws JsonProcessingException {
        CreatePostCommand createPostCommand = new CreatePostCommand();
        createPostCommand.setTopic(action.getRandomStr());
        createPostCommand.setContent(action.getRandomStr());
        createPostCommand.setTitle(action.getRandomStr());
        String s = mapper.writeValueAsString(createPostCommand);
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(0, exchange.getHeaders().get("Location"));
    }

    @Test
    public void read_post_by_topic() {
        String randomStr = action.getRandomStr();
        action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts?topic=" + randomStr + "&pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        ParameterizedTypeReference<List<PostCard>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<PostCard>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, null, responseType);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(1, exchange.getBody().size());
    }

    @Test
    public void read_post_by_user() {
        String randomStr = action.getRandomStr();
        action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts?pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        ParameterizedTypeReference<List<PostCard>> responseType = new ParameterizedTypeReference<>() {
        };
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<PostCard>> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, responseType);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(0, exchange.getBody().size());
    }

    @Test
    public void read_post_by_id() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts/" + post;
        ResponseEntity<PostDetailRepresentation> exchange = action.restTemplate.exchange(url, HttpMethod.GET, null, PostDetailRepresentation.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals("", exchange.getBody().getContent());
    }

    @Test
    public void update_post() {
        String randomStr = action.getRandomStr();
        String randomStr2 = action.getRandomStr();
        UpdatePostCommand updatePostCommand = new UpdatePostCommand();
        updatePostCommand.setContent(randomStr2);
        String post = action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post;
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        String s = null;
        try {
            s = mapper.writeValueAsString(updatePostCommand);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts/" + post;
        ResponseEntity<PostDetailRepresentation> exchange2 = action.restTemplate.exchange(url2, HttpMethod.GET, null, PostDetailRepresentation.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertEquals(randomStr2, exchange2.getBody().getContent());
    }

    @Test
    public void delete_post() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post;
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}
