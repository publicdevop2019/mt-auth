package com.hw.integration.identityaccess.proxy;

import static com.hw.helper.AppConstant.CLIENT_MNGMT_URL;
import static com.hw.helper.utility.TestContext.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hw.helper.AppConstant;
import com.hw.helper.Client;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UserUtility;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this integration auth requires oauth2service to be running.
 */
@Slf4j
@RunWith(SpringRunner.class)
public class ClientApiSecurityTest {
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
    public void should_not_able_to_create_client_w_admin_account_when_going_through_proxy()
        throws JsonProcessingException {
        Client client = ClientUtility.getClientAsNonResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(UserUtility.getJwtUser());
        String s = mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate()
                .exchange(CLIENT_MNGMT_URL, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

}
