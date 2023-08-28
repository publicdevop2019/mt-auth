package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
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
import org.springframework.web.client.RestTemplate;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})

@Slf4j
public class MgmtUtilityTest{
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }
    @Test
    public void can_clean_cache() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(AppConstant.ACCESS_URL + "/cache/clean", HttpMethod.POST, null,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_get_registry() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtAdmin);
        HttpEntity<Void> voidRequestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(HttpUtility.getAccessUrl("registry"),
                HttpMethod.GET, voidRequestEntity, new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_get_csrf_value() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(HttpUtility.getAccessUrl("csrf"),
                HttpMethod.GET, null, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_call_expire_check_api() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String userJwt = UserUtility.getJwtUser();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(userJwt);
        HttpEntity<Void> voidRequestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(HttpUtility.getAccessUrl("expire/check"),
                HttpMethod.GET, voidRequestEntity, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_call_access_health_check() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(HttpUtility.appendPath(AppConstant.ACCESS_URL, "health"),
                HttpMethod.GET, null,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<Void> exchange2 =
            restTemplate.exchange(HttpUtility.getAccessUrl("health"),
                HttpMethod.GET, null, Void.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange2.getStatusCode());
    }
}
