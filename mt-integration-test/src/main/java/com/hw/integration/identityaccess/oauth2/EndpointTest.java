package com.hw.integration.identityaccess.oauth2;

import com.hw.helper.EndpointInfo;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this integration auth requires oauth2service to be running.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class EndpointTest {
    public static final String ENDPOINTS = "/projects/0P8HE307W6IO/endpoints";
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

    private static ResponseEntity<SumTotal<EndpointInfo>> readEndpoints() {
        String url = UrlUtility.getAccessUrl(ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<EndpointInfo> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<EndpointInfo> readEndpoint(String id) {
        String url = UrlUtility.getAccessUrl(ENDPOINTS + "/" + id);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<EndpointInfo> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, EndpointInfo.class);
    }

    @Test
    @Ignore
    public void modify_existing_endpoint_to_prevent_access() {
        String url2 = UrlUtility.getAccessUrl("/users/admin");
        //before modify, admin is able to access resourceOwner apis
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange1 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());
        //modify profile to prevent admin access
        ResponseEntity<SumTotal<EndpointInfo>> listResponseEntity = readEndpoints();
        EndpointInfo endpointInfo = listResponseEntity.getBody().getData().get(6);
        endpointInfo.getUserRoles().remove("ROLE_ADMIN");
        endpointInfo.getUserRoles().add("ROLE_ROOT");

        ResponseEntity<String> stringResponseEntity =
            updateEndpoint(endpointInfo, endpointInfo.getId());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
        //after modify, admin is not able to access resourceOwner apis
        try {
            Thread.sleep(15 * 1000);//wait for cache update
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
        //modify profile to allow access
        endpointInfo.getUserRoles().remove("ROLE_ROOT");
        endpointInfo.getUserRoles().add("ROLE_ADMIN");
        endpointInfo.setVersion(endpointInfo.getVersion() + 1);
        ResponseEntity<String> stringResponseEntity1 =
            updateEndpoint(endpointInfo, endpointInfo.getId());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity1.getStatusCode());
        try {
            Thread.sleep(15 * 1000);//wait for cache update
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_new_endpoint_then_delete() {
        EndpointInfo endpoint = new EndpointInfo();
        endpoint.setResourceId("0C8AZTODP4HT");
        endpoint.setUserRoles(new HashSet<>(List.of("ROLE_ADMIN")));
        endpoint.setUserOnly(true);
        endpoint.setName("test");
        endpoint.setMethod("GET");
        endpoint.setWebsocket(false);
        endpoint
            .setPath(
                "/test/" + UUID.randomUUID().toString().replace("-", "").replaceAll("\\d", "")
                    +
                    "/abc");
        ResponseEntity<String> profile = EndpointUtility.createEndpoint(endpoint);
        Assert.assertEquals(HttpStatus.OK, profile.getStatusCode());
        ResponseEntity<String> stringResponseEntity =
            deleteEndpoint(profile.getHeaders().getLocation().toString());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
    }

    private ResponseEntity<String> updateEndpoint(EndpointInfo endpointInfo, String id) {
        String url = UrlUtility.getAccessUrl(ENDPOINTS + "/" + id);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<EndpointInfo> hashMapHttpEntity1 = new HttpEntity<>(endpointInfo, headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, hashMapHttpEntity1, String.class);
    }

    private ResponseEntity<String> deleteEndpoint(String id) {
        String url = UrlUtility.getAccessUrl(ENDPOINTS + "/" + id);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.DELETE, hashMapHttpEntity1, String.class);
    }
}
