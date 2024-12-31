package com.mt.integration.single.proxy;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class GatewayFilterTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void should_get_etag_for_get_resources() {
        String url2 = HttpUtility.getTestUrl("/cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String eTag = exchange2.getHeaders().getETag();
        Assertions.assertNotNull(eTag);
    }

    @Test
    public void should_get_cache_control_for_get_resources() {
        String url2 = HttpUtility.getTestUrl("cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 = TestContext.getRestTemplate()
            .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String cacheControl = exchange2.getHeaders().getCacheControl();
        Assertions.assertNotNull(cacheControl);
        long expires = exchange2.getHeaders().getExpires();
        Assertions.assertEquals(-1L, expires);
        String pragma = exchange2.getHeaders().getPragma();
        Assertions.assertNull(pragma);
    }

    @Test
    public void should_get_gzip_for_get_resources_more_then_1kb() {
        String url2 = HttpUtility.getAccessUrl(AppConstant.CLIENTS);
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
        Assertions.assertEquals("gzip", eTag);
    }

    @Test
    public void should_get_304_when_etag_present() {
        String url = HttpUtility.getTestUrl("/cache");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, hashMapHttpEntity1, String.class);
        String etag = exchange2.getHeaders().getETag();
        log.info("etag returned {}", etag);
        headers1.setIfNoneMatch(Objects.requireNonNull(etag));
        HttpEntity<Object> hashMapHttpEntity2 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange3 =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, hashMapHttpEntity2, String.class);
        Assertions.assertEquals(HttpStatus.NOT_MODIFIED, exchange3.getStatusCode());
    }

    @Test
    public void should_sanitize_request_json_replace_single_quote_with_double_quote() {
        String url = HttpUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        headers1.setContentType(MediaType.APPLICATION_JSON);
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"test\":'test'}", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assertions.assertEquals("{\"test\":\"test\"}",
            exchange.getBody());
    }

    @Test
    public void should_sanitize_response_json_replace_single_quote_with_double_quote() {
        String url = HttpUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("{\"test\":'test'}", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assertions.assertEquals("{\"test\":\"test\"}",
            exchange.getBody());
    }

    @Test
    public void should_allow_public_access() {
        String url =
            HttpUtility.getTestUrl("/get/hello");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertNotEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    @Test
    public void should_cut_off_api_when_max_limit_reach() {
        String url = HttpUtility.getTestUrl("/delay/" + "16000");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getStatusCode());
    }

    @Test
    public void should_has_no_response_body_when_500() {
        String url = HttpUtility.getTestUrl("/status/500");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getStatusCode());
        Assertions.assertNull(exchange.getBody());
    }

    @Test
    public void should_ask_for_csrf_token_when_post() {
        String url = HttpUtility.getTestUrl("post");
        HttpHeaders headers1 = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>("Test", headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
        headers1.set("X-XSRF-TOKEN", "123");
        headers1.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<String> hashMapHttpEntity2 = new HttpEntity<>("Test", headers1);
        ResponseEntity<String> exchange2 =
            restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity2, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void should_return_not_found_when_call_not_exist_ep() {
        String url = HttpUtility.getAccessUrl(RandomUtility.randomHttpPath());
        HttpHeaders headers1 = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> hashMapHttpEntity1 = new HttpEntity<>(null, headers1);
        ResponseEntity<String> exchange =
            restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exchange.getStatusCode());
    }

}
