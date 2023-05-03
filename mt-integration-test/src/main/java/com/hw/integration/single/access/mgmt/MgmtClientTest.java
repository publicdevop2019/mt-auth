package com.hw.integration.single.access.mgmt;

import static com.hw.helper.AppConstant.CLIENTS;
import static com.hw.helper.AppConstant.MGMT_CLIENTS;

import com.hw.helper.Client;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import java.util.Objects;
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

@RunWith(SpringRunner.class)
@Slf4j
public class MgmtClientTest extends CommonTest {

    @Test
    public void admin_can_read_client() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_CLIENTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());
    }

    @Test
    public void admin_can_view_client_detail() {
        //read first page
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        String accessUrl = UrlUtility.getAccessUrl(MGMT_CLIENTS);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        //get random page
        String randomPageUrl = RandomUtility.pickRandomPage(accessUrl,
            Objects.requireNonNull(exchange.getBody()), null);
        log.info("page url is {}",randomPageUrl);
        ResponseEntity<SumTotal<Client>> exchange3 = TestContext.getRestTemplate()
            .exchange(randomPageUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, Objects.requireNonNull(exchange3.getBody()).getData().size());
        //get random client
        int size = exchange3.getBody().getData().size();
        log.info("size is {}",size);
        int picked = RandomUtility.pickRandomFromList(size);
        log.info("picked index {}",picked);
        String clientId = exchange3.getBody().getData().get(picked).getId();
        ResponseEntity<Client> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(UrlUtility.combinePath(CLIENTS, clientId)),
                HttpMethod.GET, request,
                Client.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        log.info("body {}",exchange2.getBody());
        Assert.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getGrantTypeEnums());
    }
}
