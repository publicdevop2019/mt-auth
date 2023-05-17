package com.mt.test_case.integration.single.access.mgmt;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.CommonTest;
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
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@Slf4j
public class MgmtUtilityTest extends CommonTest {
    @Test
    public void can_clean_cache() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(AppConstant.ACCESS_URL + "/cache/clean", HttpMethod.POST, null,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_get_registry() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String jwtAdmin = UserUtility.getJwtAdmin();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtAdmin);
        HttpEntity<Void> voidRequestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(UrlUtility.getAccessUrl("registry"),
                HttpMethod.GET, voidRequestEntity, new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_get_csrf_value() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(UrlUtility.getAccessUrl("csrf"),
                HttpMethod.GET, null, Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_call_expire_check_api() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        String userJwt = UserUtility.getJwtUser();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(userJwt);
        HttpEntity<Void> voidRequestEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Void> exchange =
            restTemplate.exchange(UrlUtility.getAccessUrl("expire/check"),
                HttpMethod.GET, voidRequestEntity, Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void can_call_access_health_check() {
        RestTemplate restTemplate = TestContext.getRestTemplate();
        ResponseEntity<Void> exchange =
            restTemplate.exchange(UrlUtility.appendPath(AppConstant.ACCESS_URL, "health"),
                HttpMethod.GET, null,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<Void> exchange2 =
            restTemplate.exchange(UrlUtility.getAccessUrl("health"),
                HttpMethod.GET, null, Void.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange2.getStatusCode());
    }
}
