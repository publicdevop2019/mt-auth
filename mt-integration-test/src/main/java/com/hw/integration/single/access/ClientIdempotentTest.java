package com.hw.integration.single.access;

import static com.hw.helper.AppConstant.CLIENTS;
import static com.hw.helper.utility.ConcurrentUtility.assertConcurrent;

import com.hw.helper.AppConstant;
import com.hw.helper.Client;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class ClientIdempotentTest  extends CommonTest {


    @Test
    public void create_client_w_same_changeId_two_times() {
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        String s = UUID.randomUUID().toString();
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        ResponseEntity<String> client2 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client2.getStatusCode());
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        String s2 = UUID.randomUUID().toString();
        headers.set("changeId", s2);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
        oldClient.setVersion(0);
        HttpEntity<Client> request = new HttpEntity<>(oldClient, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        oldClient.setVersion(1);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_client_w_same_changeId_two_times_concurrent() {
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        String s = UUID.randomUUID().toString();
        AtomicReference<Integer> success = new AtomicReference<>(0);
        AtomicReference<Integer> failed = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            TestContext.init();
            ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
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
            assertConcurrent("", runnables, 30000);
            Assert.assertEquals(1, (int) success.get());
            Assert.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_update_w_same_changeId_two_times_concurrent() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
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
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
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
            assertConcurrent("", runnables, 30000);
            Assert.assertEquals(1, (int) success.get());
            Assert.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_client_then_delete_w_same_changeId_two_times_concurrent() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        String s = UUID.randomUUID().toString();
        Client oldClient = ClientUtility.getClientAsResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        ResponseEntity<String> client1 = ClientUtility.createClient(oldClient, s);
        Assert.assertEquals(HttpStatus.OK, client1.getStatusCode());
        oldClient.setAccessTokenValiditySeconds(120);
        String url =
            UrlUtility.getAccessUrl(CLIENTS + "/" + client1.getHeaders().getLocation().toString());
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
            assertConcurrent("", runnables, 30000);
            Assert.assertEquals(1, (int) success.get());
            Assert.assertEquals(1, (int) failed.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
