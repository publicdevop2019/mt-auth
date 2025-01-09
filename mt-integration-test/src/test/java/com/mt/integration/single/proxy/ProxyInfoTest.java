package com.mt.integration.single.proxy;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class ProxyInfoTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void get_check_sum_value() {
        String url = HttpUtility.getProxyUrl("info/checkSum");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}
