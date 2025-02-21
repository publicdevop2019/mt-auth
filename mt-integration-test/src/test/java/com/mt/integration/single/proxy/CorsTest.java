package com.mt.integration.single.proxy;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * this test suits requires cors profile to be added.
 */

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class CorsTest {
    private final String thirdPartyOrigin = "http://localhost:4300";
    private final String[] corsUris = {"/oauth/token", "/mgmt/clients/0", "/mgmt/clients",
        "/authorize", "/mgmt/users", "/mgmt/users/0", "/users/pwd", "/users"};

    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void cors_oauthToken() {
        String url = HttpUtility.getAccessUrl(corsUris[0]);
        ResponseEntity<?> res = sendValidCorsForTokenUri(url);
        corsAssertToken(res);
    }


    @Test
    public void cors_clients() {
        String url = HttpUtility.getAccessUrl(corsUris[2]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        Assertions.assertEquals("[GET]",
            res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_authorize() {
        String url = HttpUtility.getAccessUrl(corsUris[3]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.POST);
        Assertions.assertEquals("[POST]",
            res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_resourceOwner() {
        String url = HttpUtility.getAccessUrl(corsUris[4]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        Assertions.assertEquals("[GET]",
            res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_resourceOwner_id_put() {
        String url = HttpUtility.getAccessUrl(corsUris[5]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.PUT);
        Assertions.assertEquals("[PUT]",
            res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    private ResponseEntity<?> sendValidCorsForTokenUri(String uri) {
        //origin etc restricted headers will not be set by HttpUrlConnection,
        //ref:https://stackoverflow.com/questions/41699608/resttemplate-not-passing-origin-header
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", thirdPartyOrigin);
        headers.add("Access-Control-Request-Method", "POST");
        headers.add("Access-Control-Request-Headers", "authorization");
        HttpEntity<String> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate()
            .exchange(uri, HttpMethod.OPTIONS, request, String.class);

    }

    private void corsAssertToken(ResponseEntity res) {
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions
            .assertEquals("http://localhost:4300", res.getHeaders().getAccessControlAllowOrigin());
        Assertions.assertEquals("[POST]",
            res.getHeaders().getAccessControlAllowMethods().toString());
        Assertions.assertEquals("[authorization]",
            res.getHeaders().getAccessControlAllowHeaders().toString());
        Assertions.assertEquals(7200, res.getHeaders().getAccessControlMaxAge());
    }

    private ResponseEntity<?> sendValidCorsForNonTokenUri(String uri, HttpMethod method) {
        /**
         * origin etc restricted headers will not be set by HttpUrlConnection,
         * ref:https://stackoverflow.com/questions/41699608/resttemplate-not-passing-origin-header
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", thirdPartyOrigin);
        headers.add("Access-Control-Allow-Origin", "http://localhost:4300");
        headers.add("Access-Control-Request-Method", method.toString());
        headers.add("Access-Control-Request-Headers", "authorization");
        HttpEntity<String> request = new HttpEntity<>(headers);
        return TestContext.getRestTemplate()
            .exchange(uri, HttpMethod.OPTIONS, request, String.class);

    }

    private void corsAssertNonToken(ResponseEntity res) {
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions
            .assertEquals("http://localhost:4300", res.getHeaders().getAccessControlAllowOrigin());
        Assertions.assertEquals("[authorization]",
            res.getHeaders().getAccessControlAllowHeaders().toString());
        Assertions.assertEquals(7200, res.getHeaders().getAccessControlMaxAge());
    }

}
