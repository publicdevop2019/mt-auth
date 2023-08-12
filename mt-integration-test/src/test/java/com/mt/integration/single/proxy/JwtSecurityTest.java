package com.mt.integration.single.proxy;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class JwtSecurityTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }

    @Test
    public void user_modify_jwt_token_after_login() {
        String defaultUserToken = UserUtility.registerNewUserThenLogin();
        String url = HttpUtility.getAccessUrl("/status/200");
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, getHttpRequest(defaultUserToken + "valueChange"),
                String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getStatusCode());
    }

    @Test
    public void trying_access_protected_api_without_jwt_token() {
        String url = HttpUtility.getAccessUrl("/status/200");
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, getHttpRequest(null), String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exchange.getStatusCode());
    }

    private HttpEntity<?> getHttpRequest(String authorizeToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(authorizeToken);
        return new HttpEntity<>(headers);
    }

}
