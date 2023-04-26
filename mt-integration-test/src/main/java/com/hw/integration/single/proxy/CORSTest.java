package com.hw.integration.single.proxy;

import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this test suits requires cors profile to be added.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class CORSTest {
    private final String thirdPartyOrigin = "http://localhost:4300";
    private final String[] corsUris = {"/oauth/token", "/mgmt/clients/0", "/mgmt/clients",
        "/authorize", "/mgmt/users", "/mgmt/users/0", "/users/pwd", "/users"};
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
    public void cors_oauthToken() {
        String url = UrlUtility.getAccessUrl(corsUris[0]);
        ResponseEntity<?> res = sendValidCorsForTokenUri(url);
        corsAssertToken(res);
    }


    @Test
    public void cors_clients() {
        String url = UrlUtility.getAccessUrl(corsUris[2]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        Assert.assertEquals("[GET]", res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_authorize() {
        String url = UrlUtility.getAccessUrl(corsUris[3]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.POST);
        Assert.assertEquals("[POST]", res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_resourceOwner() {
        String url = UrlUtility.getAccessUrl(corsUris[4]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.GET);
        Assert.assertEquals("[GET]", res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_resourceOwner_id_put() {
        String url = UrlUtility.getAccessUrl(corsUris[5]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.PUT);
        Assert.assertEquals("[PUT]", res.getHeaders().getAccessControlAllowMethods().toString());
        corsAssertNonToken(res);

    }

    @Test
    public void cors_resourceOwner_id_delete() {
        String url = UrlUtility.getAccessUrl(corsUris[5]);
        ResponseEntity<?> res = sendValidCorsForNonTokenUri(url, HttpMethod.DELETE);
        Assert.assertEquals("[DELETE]", res.getHeaders().getAccessControlAllowMethods().toString());
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
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assert
            .assertEquals("http://localhost:4300", res.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("[POST]", res.getHeaders().getAccessControlAllowMethods().toString());
        Assert.assertEquals("[authorization]",
            res.getHeaders().getAccessControlAllowHeaders().toString());
        Assert.assertEquals(86400, res.getHeaders().getAccessControlMaxAge());
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
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assert
            .assertEquals("http://localhost:4300", res.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("[authorization]",
            res.getHeaders().getAccessControlAllowHeaders().toString());
        Assert.assertEquals(86400, res.getHeaders().getAccessControlMaxAge());
    }

}
