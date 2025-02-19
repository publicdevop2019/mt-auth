package com.mt.integration.single.access.mgmt;

import static com.mt.helper.AppConstant.CLIENT_ID_OAUTH2_ID;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.BellNotification;
import com.mt.helper.pojo.CheckSum;
import com.mt.helper.pojo.Job;
import com.mt.helper.pojo.RevokeToken;
import com.mt.helper.pojo.StoredEvent;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.SystemNotification;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})

@Slf4j
public class MgmtTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    private static HttpEntity<String> getAdminHttpEntity() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(null, headers);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void admin_can_view_system_bell_notification() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<BellNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_BELL),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_job_info() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).length);
    }

    @Test
    public void admin_can_reset_job_status() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Job[] body = exchange.getBody();
        assert body != null;
        int i = RandomUtility.pickRandomFromList(body.length);
        Job job = body[i];
        String id = job.getId();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(HttpUtility.combinePath(
                    AppConstant.MGMT_JOBS, id + "/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

    }

    @Test
    public void admin_can_rest_data_validation_failure_count() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(HttpUtility.combinePath("mgmt/job", "validation/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_tokens_revoked() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<RevokeToken>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_TOKENS),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_check_proxy_cache_md5() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<CheckSum> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_PROXY_CHECK),
                HttpMethod.GET, request,
                CheckSum.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assertions.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getHostValue());
    }

    @Test
    public void admin_can_reload_proxy_cache() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_PROXY_RELOAD),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_EVENT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_audit_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_EVENT_AUDIT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_failure_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(
                    HttpUtility.appendQuery(AppConstant.MGMT_EVENT, "query=rejected:1")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_unroutable_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(
                    HttpUtility.appendQuery(AppConstant.MGMT_EVENT, "query=routable:0")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_notification_history() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<SystemNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(AppConstant.MGMT_NOTIFICATION),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void get_websocket_ticket() {
        String url = HttpUtility.getAccessUrl("tickets");
        String finalUrl = HttpUtility.appendPath(url, CLIENT_ID_OAUTH2_ID);
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Object> objectHttpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(finalUrl, HttpMethod.POST, objectHttpEntity, Void.class);
        URI location = exchange.getHeaders().getLocation();
        String s = location.toString();
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assertions.assertNotNull(s);
    }

    @Test
    public void send_websocket_call() throws InterruptedException {
        String url = HttpUtility.getAccessUrl("tickets");
        String finalUrl = HttpUtility.appendPath(url, CLIENT_ID_OAUTH2_ID);
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Object> objectHttpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(finalUrl, HttpMethod.POST, objectHttpEntity, Void.class);
        URI location = exchange.getHeaders().getLocation();
        String wsToken = Base64.toBase64String(location.toString().getBytes());
        String wsUrl = HttpUtility.getAccessSocketUrl("monitor");
        String wsUrlFinal = HttpUtility.appendQuery(wsUrl, "jwt=" + wsToken);
        OkHttpClient client = new OkHttpClient();
        CountDownLatch latch = new CountDownLatch(1);
        final String[] receivedMessage = new String[1];
        Request request = new Request.Builder()
            .url(wsUrlFinal)
            .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("connection established");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                log.info("message received: {}", text);
                receivedMessage[0] = text;
                latch.countDown();
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("Connection closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
                latch.countDown();
            }
        };

        WebSocket webSocket = client.newWebSocket(request, listener);
        // Wait for the latch or timeout
        boolean success = latch.await(30, TimeUnit.SECONDS);
        // Close WebSocket after test
        webSocket.close(1000, "Test complete");
        Assertions.assertTrue(success);
        Assertions.assertEquals("_renew", receivedMessage[0]);
    }
}
