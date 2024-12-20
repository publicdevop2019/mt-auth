package com.mt.integration.concurrent;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@Disabled
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class ClientIdempotentTest {

    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void create_client_w_same_changeId_two_times() {
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        String s = UUID.randomUUID().toString();
        String jwtAdmin = UserUtility.getJwtAdmin();
        ResponseEntity<String> client1 = ClientUtility.createClient(jwtAdmin, oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        ResponseEntity<String> client2 = ClientUtility.createClient(jwtAdmin, oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 =
            ClientUtility.createClient(UserUtility.getJwtAdmin(), oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s2 = UUID.randomUUID().toString();
        headers.set("changeId", s2);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        String url =
            HttpUtility.getAccessUrl(AppConstant.CLIENTS + "/" + HttpUtility.getId(client1));
        oldClient.setVersion(0);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        oldClient.setVersion(1);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 =
            ClientUtility.createClient(UserUtility.getJwtAdmin(), oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        String url =
            HttpUtility.getAccessUrl(AppConstant.CLIENTS + "/" + HttpUtility.getId(client1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s2 = UUID.randomUUID().toString();
        headers.set("changeId", s2);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_client_w_same_changeId_two_times_concurrent() {
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        String s = UUID.randomUUID().toString();
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        String jwtAdmin = UserUtility.getJwtAdmin();
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<String> client1 = ClientUtility.createClient(jwtAdmin, oldClient, s);
            if (client1.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (client1.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times_concurrent() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 =
            ClientUtility.createClient(UserUtility.getJwtAdmin(), oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        HttpHeaders headers = new HttpHeaders();
        String bearer = tokenResponse.getBody().getValue();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s2 = UUID.randomUUID().toString();
        headers.set("changeId", s2);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        oldClient.setVersion(0);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        String url =
            HttpUtility.getAccessUrl(AppConstant.CLIENTS + "/" + HttpUtility.getId(client1));
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<String> exchange =
                TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (exchange.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times_concurrent() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.emailPwdLogin(
            AppConstant.ACCOUNT_EMAIL_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 =
            ClientUtility.createClient(UserUtility.getJwtAdmin(), oldClient, s);
        Assertions.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        String url =
            HttpUtility.getAccessUrl(AppConstant.CLIENTS + "/" + HttpUtility.getId(client1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s2 = UUID.randomUUID().toString();
        headers.set("changeId", s2);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<String> exchange = TestContext.getRestTemplate()
                .exchange(url, HttpMethod.DELETE, request, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                success.set(success.get() + 1);
            }
            if (exchange.getStatusCode().is4xxClientError()) {
                failed.set(failed.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        runnables.add(runnable2);
        runnables.add(runnable2);
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertEquals(1, (int) success.get());
            Assertions.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
