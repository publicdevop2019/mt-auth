package com.mt.integration.performance;

import static com.mt.helper.TestHelper.RUN_ID;

import com.mt.helper.AppConstant;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class DifUserLoginPerformanceTest {
    @Test
    public void login() {
        String s = UUID.randomUUID().toString();
        MDC.clear();
        MDC.put(RUN_ID, s);
        TestContext.init();
        log.info("run id {}", s);
        List<User> users = new ArrayList<>();
        int numOfUser = 20;
        IntStream.range(0, numOfUser).forEach(i -> {
            User user = UserUtility.createEmailPwdUser();
            log.info("created user id {}", user.getId());
            users.add(user);
        });
        log.info("create user completed");
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger timeoutCount = new AtomicInteger(0);
        AtomicInteger index = new AtomicInteger();
        Runnable runnable = () -> {
            log.info("start of dif user login");
            TestContext.init();
            int andIncrement = index.getAndIncrement();
            User user = users.get(andIncrement);

            ResponseEntity<DefaultOAuth2AccessToken> response = OAuth2Utility
                .getPasswordFlowEmailPwdToken(AppConstant.CLIENT_ID_LOGIN_ID,
                    AppConstant.COMMON_CLIENT_SECRET,
                    user.getEmail(),
                    user.getPassword());

            if (response.getStatusCode().value() == 504) {
                timeoutCount.getAndIncrement();
            }
            if (response.getStatusCode().value() != 200) {
                failCount.getAndIncrement();
            }
            log.info("login response {}", response.getStatusCode().value());
            log.info("end of dif user login");
        };
        List<Runnable> runnableList = new ArrayList<>();
        IntStream.range(0, numOfUser).forEach(e -> {
            runnableList.add(runnable);
        });
        try {
            ConcurrentUtility.assertConcurrent("", runnableList, 30000);
            Assertions.assertEquals(0, failCount.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
