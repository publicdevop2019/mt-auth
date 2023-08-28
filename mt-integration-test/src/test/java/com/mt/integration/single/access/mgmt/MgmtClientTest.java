package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
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
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})

@Slf4j
public class MgmtClientTest{
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }
    @Test
    public void admin_can_read_client() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_CLIENTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());
    }

    @Test
    public void admin_can_view_client_detail() {
        //read first page
        String token =
            UserUtility.getJwtAdmin();
        log.info("admin token is {}", token);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        String accessUrl = HttpUtility.getAccessUrl(AppConstant.MGMT_CLIENTS);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(accessUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assertions.assertNotNull(exchange.getBody());
        //get random page
        String randomPageUrl = RandomUtility.pickRandomPage(accessUrl,
            exchange.getBody(), 50);
        log.info("page url is {}", randomPageUrl);
        ResponseEntity<SumTotal<Client>> exchange3 = TestContext.getRestTemplate()
            .exchange(randomPageUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange3.getBody()).getData().size());
        //get random client
        int size = exchange3.getBody().getData().size();
        log.info("size is {}", size);
        int picked = RandomUtility.pickRandomFromList(size);
        String clientId = exchange3.getBody().getData().get(picked).getId();
        log.info("picked client id {}", clientId);
        ResponseEntity<Client> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(HttpUtility.combinePath(AppConstant.MGMT_CLIENTS, clientId)),
                HttpMethod.GET, request,
                Client.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        log.info("body {}", exchange2.getBody());
        Assertions.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getGrantTypeEnums());
    }
}
