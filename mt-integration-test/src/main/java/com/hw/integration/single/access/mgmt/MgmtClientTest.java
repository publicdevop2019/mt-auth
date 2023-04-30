package com.hw.integration.single.access.mgmt;

import static com.hw.helper.AppConstant.CLIENTS;

import com.hw.helper.Client;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class MgmtClientTest extends CommonTest {

    @Test
    public void admin_can_read_client() {
        ResponseEntity<DefaultOAuth2AccessToken> jwtPasswordAdmin =
            UserUtility.getJwtPasswordAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtPasswordAdmin.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Client>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(CLIENTS), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }

    @Test
    public void admin_can_view_client_detail(){

    }
}
