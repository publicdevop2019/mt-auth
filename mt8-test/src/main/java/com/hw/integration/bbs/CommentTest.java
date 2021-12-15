package com.hw.integration.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.CommentPrivateCard;
import com.hw.helper.CommentPublicCard;
import com.hw.helper.CreateCommentCommand;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentTest {
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
    public void create_comment() {
        String randomStr = action.getRandomStr();
        String randomStr2 = action.getRandomStr();
        CreateCommentCommand createCommentCommand = new CreateCommentCommand();
        createCommentCommand.setContent(randomStr2);
        String post = action.createPost(randomStr);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/comments";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        String s = null;
        try {
            s = mapper.writeValueAsString(createCommentCommand);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void create_comment_w_invalid_reference() {
        String randomStr2 = action.getRandomStr();
        CreateCommentCommand createCommentCommand = new CreateCommentCommand();
        createCommentCommand.setContent(randomStr2);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + new Random().nextInt(100) + "/comments";
        String s1 = action.getBbsRootToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        String s = null;
        try {
            s = mapper.writeValueAsString(createCommentCommand);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void delete_comment() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        action.createCommentForPost(post);

        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/comments?pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        HttpHeaders headers = new HttpHeaders();
        String s1 = action.getBbsRootToken();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<List<CommentPrivateCard>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CommentPrivateCard>> exchange = action.restTemplate.exchange(url2, HttpMethod.GET, request, responseType);

        Long id = exchange.getBody().get(0).getId();

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/comments/"+id;

        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void delete_comment_w_someone_else_id() {
        String randomStr = action.getRandomStr();
        String bbsAdminToken = action.getBbsAdminToken();
        String bbsRootToken = action.getBbsRootToken();
        String post = action.createPost(randomStr);
        action.createCommentForPost(post,bbsAdminToken);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/comments?pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bbsAdminToken);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<List<CommentPrivateCard>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CommentPrivateCard>> exchange = action.restTemplate.exchange(url2, HttpMethod.GET, request, responseType);

        Long id = exchange.getBody().get(0).getId();

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/comments/"+id;
        HttpHeaders headers2 = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bbsRootToken);
        HttpEntity<String> request2 = new HttpEntity<>(null, headers2);
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url, HttpMethod.DELETE, request2, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange2.getStatusCode());

    }

    @Test
    public void get_comments_for_reference() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        action.createCommentForPost(post);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts/" + post + "/comments?pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<List<CommentPublicCard>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CommentPublicCard>> exchange = action.restTemplate.exchange(url2, HttpMethod.GET, request, responseType);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(0, exchange.getBody().size());
    }

    @Test
    public void get_comments_for_user() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String post2 = action.createPost(randomStr);
        action.createCommentForPost(post);
        action.createCommentForPost(post2);
        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/comments?pageNum=0&pageSize=20&sortBy=id&sortOrder=asc";
        HttpHeaders headers = new HttpHeaders();
        String s1 = action.getBbsRootToken();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ParameterizedTypeReference<List<CommentPrivateCard>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CommentPrivateCard>> exchange = action.restTemplate.exchange(url2, HttpMethod.GET, request, responseType);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(1, exchange.getBody().size());

    }
}
