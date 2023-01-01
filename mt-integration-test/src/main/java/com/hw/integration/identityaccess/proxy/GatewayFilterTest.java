package com.hw.integration.identityaccess.proxy;

import static com.hw.helper.AppConstant.CLIENTS;

import com.hw.helper.utility.ConcurrentUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@Slf4j
public class GatewayFilterTest {
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, id {}", description.getMethodName(),
                TestContext.getTestId());
        }
    };

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    public void should_get_etag_for_get_resources() {
        String url2 = UrlUtility.getTestUrl("/cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String eTag = exchange2.getHeaders().getETag();
        Assert.assertNotNull(eTag);
    }

    @Test
    public void should_get_cache_control_for_get_resources() {
        String url2 = UrlUtility.getTestUrl("cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String cacheControl = exchange2.getHeaders().getCacheControl();
        Assert.assertNotNull(cacheControl);
        long expires = exchange2.getHeaders().getExpires();
        Assert.assertEquals(-1L, expires);
        String pragma = exchange2.getHeaders().getPragma();
        Assert.assertNull(pragma);
    }

    @Test
    public void should_get_gzip_for_get_resources_more_then_1kb() {
        String url2 = UrlUtility.getAccessUrl(CLIENTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        headers1.set("changeId", UUID.randomUUID().toString());
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        //use regular RestTemplate so headers can be retrieved
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange2;
        try {
            exchange2 =
                restTemplate.exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        } catch (Exception ex) {
            log.error("error during call", ex);
            throw ex;
        }
        String eTag = exchange2.getHeaders().getFirst("Content-Encoding");
        Assert.assertEquals("gzip", eTag);
    }

    @Test
    public void should_get_304_when_etag_present() {
        String url2 = UrlUtility.getTestUrl("/cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String etag = exchange2.getHeaders().getETag();
        headers1.setIfNoneMatch(Objects.requireNonNull(etag));
        HttpEntity<Object> hashMapHttpEntity2 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity2, String.class);
        Assert.assertEquals(HttpStatus.NOT_MODIFIED, exchange3.getStatusCode());
    }

    @Test
    public void should_sanitize_request_json_replace_single_quote_with_double_quote() {
        String url = UrlUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        headers1.setContentType(MediaType.APPLICATION_JSON);
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"test\":'test'}", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals("{\"test\":\"test\"}",
            exchange.getBody());
    }

    @Test
    public void should_sanitize_response_json_replace_single_quote_with_double_quote() {
        String url = UrlUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"test\":'test'}", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals("{\"test\":\"test\"}",
            exchange.getBody());
    }

    @Test
    public void should_allow_public_access() {
        String url =
            UrlUtility.getTestUrl("/get/hello");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertNotEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    @Test
    public void should_cut_off_api_when_max_limit_reach() {
        String url = UrlUtility.getTestUrl("/delay/" + "15000");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        //will get 500 instead of 504 due to proxy configured only return 500
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
    }

    @Test
    public void should_has_no_response_body_when_500() {
        String url = UrlUtility.getTestUrl("/status/500");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
        Assert.assertNull(exchange.getBody());
    }

    @Test
    public void should_ask_for_csrf_token_when_post() {
        String url = UrlUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("Test", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>("Test", headers1);
        ResponseEntity<String> exchange2 =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity2, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void should_add_csrf_token_when_call_access() {
        String url = UrlUtility.getAccessUrl("csrf");
        HttpHeaders headers1 = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String first = exchange.getHeaders().getFirst("set-cookie");
        int sameSite = first == null ? -1 : first.indexOf("SameSite");
        Assert.assertNotEquals(-1, sameSite);
    }

    @Test
    public void should_get_too_many_request_exceed_burst_rate_limit() {
        String url2 = UrlUtility.getTestUrl("get/0");
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            //need to init TestContext again due to runnable is executed in different thread pool hence TestContext threadlocal is null
            TestContext.init();
            ResponseEntity<String> exchange = TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, null, String.class);
            log.info("response status is {}", exchange.getStatusCode().value());
            log.info("rate limit left is {}", exchange.getHeaders().get("x-mt-ratelimit-left"));
            if (exchange.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                count.getAndSet(count.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            runnables.add(runnable2);
        }
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assert.assertNotEquals(0, count.get().intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
