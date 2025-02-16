package com.mt.integration.performance;

import static com.mt.helper.TestHelper.RUN_ID;

import com.mt.helper.AppConstant;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ConcurrentUtility;
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

@Disabled // for perf, enable if required
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class SameUserLoginPerformanceTest {
    @Test
    public void user_login_mfa() {
        int numOfConcurrent = 10;
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        String s = UUID.randomUUID().toString();
        MDC.clear();
        MDC.put(RUN_ID, s);
        log.info("run id {}", s);
        TestContext.init();
        User user = UserUtility.createEmailPwdUser();
        Runnable runnable = () -> {
            TestContext.init();
            log.info("start of user login");
            ResponseEntity<DefaultOAuth2AccessToken> oAuth2PasswordToken = OAuth2Utility
                .getPasswordFlowEmailPwdToken(AppConstant.CLIENT_ID_LOGIN_ID,
                    AppConstant.COMMON_CLIENT_SECRET,
                    user.getEmail(),
                    user.getPassword());
            if (oAuth2PasswordToken.getStatusCode().is4xxClientError()) {
                log.info("response body is {}", oAuth2PasswordToken.getBody());
                failCount.getAndIncrement();
            } else {
                successCount.getAndIncrement();
            }
            log.info("end of user login");
        };
        List<Runnable> runnableList = new ArrayList<>();
        IntStream.range(0, numOfConcurrent).forEach(e -> {
            runnableList.add(runnable);
        });
        try {
            ConcurrentUtility.assertConcurrent("", runnableList, 30000);
            Assertions.assertNotEquals(0, failCount.get());
            log.info("failed user login {}", failCount.get());
            log.info("success user login {}", successCount.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
