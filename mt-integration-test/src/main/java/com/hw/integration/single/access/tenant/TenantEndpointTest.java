package com.hw.integration.single.access.tenant;

import com.hw.helper.Endpoint;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
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
public class TenantEndpointTest  extends CommonTest {
    public static final String ENDPOINTS = "/projects/0P8HE307W6IO/endpoints";

    private static ResponseEntity<SumTotal<Endpoint>> readEndpoints() {
        String url = UrlUtility.getAccessUrl(ENDPOINTS);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
            });
    }

    public static ResponseEntity<Endpoint> readEndpoint(String id) {
        String url = UrlUtility.getAccessUrl(ENDPOINTS + "/" + id);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, hashMapHttpEntity1, Endpoint.class);
    }

    @Test
    public void create_new_endpoint_then_delete() {
        Endpoint endpoint = new Endpoint();
        endpoint.setResourceId("0C8AZTODP4HT");
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

    private ResponseEntity<String> updateEndpoint(Endpoint endpoint, String id) {
        String url = UrlUtility.getAccessUrl(ENDPOINTS + "/" + id);
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(UserUtility.getJwtAdmin());
        HttpEntity<Endpoint> hashMapHttpEntity1 = new HttpEntity<>(endpoint, headers1);
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

    @Test
    public void create_endpoint_then_update() {

    }
    @Test
    public void create_endpoint_then_read() {

    }

    @Test
    public void create_endpoint_for_share_then_expire_and_delete() {

    }
    @Test
    public void endpoint_validation_should_work(){

    }
}
