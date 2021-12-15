package com.hw.concurrent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.PostDetailRepresentation;
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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.hw.helper.UserAction.assertConcurrent;


@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class UserReactionServiceTest {
    @Autowired
    UserAction action;
    ObjectMapper mapper = new ObjectMapper();
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
    public void concurrent_like_same_post_same_user() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String s1 = action.getBbsRootToken();
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(200);
        integers.add(400);
        Runnable runnable = () -> {
            String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/likes";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(s1);
            HttpEntity<String> request = new HttpEntity<>(null, headers);
            ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
            Assert.assertTrue("expected status code but is " + exchange.getStatusCodeValue(), integers.contains(exchange.getStatusCodeValue()));

        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, 5).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
            String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts/" + post;
            ResponseEntity<PostDetailRepresentation> exchange = action.restTemplate.exchange(url, HttpMethod.GET, null, PostDetailRepresentation.class);
            Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
            Assert.assertEquals("1", exchange.getBody().getLikeNum().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void concurrent_like_or_dislike_same_post_same_user() {
        String randomStr = action.getRandomStr();
        String post = action.createPost(randomStr);
        String s1 = action.getBbsRootToken();
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(200);
        integers.add(400);
        Runnable runnable = () -> {
            int i = new Random().nextInt(20);
            String url2;
            if (i >= 10) {
                url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/likes";
            } else {
                url2 = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/private/posts/" + post + "/dislikes";
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(s1);
            HttpEntity<String> request = new HttpEntity<>(null, headers);
            ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.POST, request, String.class);
            Assert.assertTrue("expected status code but is " + exchange.getStatusCodeValue(), integers.contains(exchange.getStatusCodeValue()));

        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, 10).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
            String url = UserAction.proxyUrl + UserAction.SVC_NAME_BBS + "/public/posts/" + post;
            ResponseEntity<PostDetailRepresentation> exchange = action.restTemplate.exchange(url, HttpMethod.GET, null, PostDetailRepresentation.class);
            Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
            Assert.assertTrue("only one like or dislike", oneOneLikeOrDislike(exchange.getBody()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean oneOneLikeOrDislike(PostDetailRepresentation body) {
        if (body.getDislikeNum().equals(1L) && body.getLikeNum().equals(0L))
            return true;
        if (body.getDislikeNum().equals(0L) && body.getLikeNum().equals(1L))
            return true;
        return false;
    }
}
