package com.mt.integration.concurrent;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
@Disabled
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class GatewayFilterConcurrentTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void should_get_too_many_request_exceed_burst_rate_limit() {
        String url2 = HttpUtility.getTestUrl("get/0");
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Runnable runnable2 = () -> {
            //need to init TestContext again due to runnable is executed in different thread pool hence TestContext threadlocal is null
            TestContext.init();
            ResponseEntity<String> exchange = TestContext.getRestTemplate()
                .exchange(url2, HttpMethod.GET, null, String.class);
            log.trace("response status is {}", exchange.getStatusCode().value());
            log.trace("rate limit left is {}", exchange.getHeaders().get(
                AppConstant.X_MT_RATELIMIT_LEFT));
            if (exchange.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                count.getAndSet(count.get() + 1);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < 90; i++) {
            runnables.add(runnable2);
        }
        try {
            ConcurrentUtility.assertConcurrent("", runnables, 30000);
            Assertions.assertNotEquals(0, count.get().intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
