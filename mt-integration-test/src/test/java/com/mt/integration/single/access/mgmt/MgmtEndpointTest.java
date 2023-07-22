package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.CommonTest;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)

@Slf4j
public class MgmtEndpointTest extends CommonTest {
    @Test
    public void admin_can_view_all_endpoints() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Endpoint>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_ENDPOINTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());
    }

    @Test
    public void admin_can_view_endpoint_detail() {
        //including endpoint cors, cache config
        //read first page
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        String accessUrl = UrlUtility.getAccessUrl(AppConstant.MGMT_ENDPOINTS);
        ResponseEntity<SumTotal<Endpoint>> exchange = TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        //get random page
        String randomPageUrl = RandomUtility.pickRandomPage(accessUrl,
            Objects.requireNonNull(exchange.getBody()), null);
        log.info("page url is {}", randomPageUrl);
        ResponseEntity<SumTotal<Endpoint>> exchange3 = TestContext.getRestTemplate()
            .exchange(randomPageUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange3.getBody()).getData().size());
        //get random endpoint
        int size = exchange3.getBody().getData().size();
        log.info("size is {}", size);
        int picked = RandomUtility.pickRandomFromList(size);
        String epId = exchange3.getBody().getData().get(picked).getId();
        log.info("picked endpointId {}", epId);
        ResponseEntity<Endpoint> exchange4 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(UrlUtility.combinePath(AppConstant.MGMT_ENDPOINTS, epId)),
                HttpMethod.GET, request,
                Endpoint.class);
        Assertions.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
        log.info("body {}", exchange4.getBody());
        Assertions.assertNotNull(Objects.requireNonNull(exchange4.getBody()).getId());
    }
}
