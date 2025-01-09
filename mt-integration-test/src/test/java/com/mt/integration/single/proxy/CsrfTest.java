package com.mt.integration.single.proxy;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class CsrfTest {

    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }


    @Test
    public void should_add_csrf_token_when_call_token() {
        User emailPwdUser = UserUtility.createEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailPwdLoginRaw(emailPwdUser);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        String setCookieHeader = response.getHeaders().getFirst("set-cookie");
        Assertions.assertNotNull(setCookieHeader);
        Assertions.assertTrue(setCookieHeader.contains("XSRF-TOKEN"));
    }

    @Test
    public void should_not_get_csrf_value_in_none_token_endpoint() {
        String url =
            HttpUtility.getTestUrl("/get/hello");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        String setCookieHeader = exchange.getHeaders().getFirst("set-cookie");
        if (setCookieHeader != null) {
            Assertions.assertFalse(setCookieHeader.contains("XSRF-TOKEN"));
        }
    }
}
