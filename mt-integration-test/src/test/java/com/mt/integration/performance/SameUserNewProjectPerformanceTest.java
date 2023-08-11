package com.mt.integration.performance;

import static com.mt.helper.TestHelper.RUN_ID;

import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ConcurrentUtility;
import com.mt.helper.utility.ProjectUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Disabled
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class SameUserNewProjectPerformanceTest {
    @Test
    public void new_project() {
        int numOfConcurrent = 10;
        AtomicInteger failCount = new AtomicInteger(0);
        String s = UUID.randomUUID().toString();
        MDC.clear();
        MDC.put(RUN_ID, s);
        log.info("run id {}", s);
        User user = UserUtility.createRandomUserObj();
        TestContext.init();
        ResponseEntity<Void> pendingUser = UserUtility.register(user);
        String id = UrlUtility.getId(pendingUser);
        log.info("user id {}", id);
        user.setId(id);
        String login = UserUtility.login(user);
        log.info("login token {}", login);
        Runnable runnable = () -> {
            TestContext.init();
            log.info("start of creating project");
            Project randomProjectObj = ProjectUtility.createRandomProjectObj();
            ResponseEntity<Void> tenantProject =
                ProjectUtility.createTenantProject(randomProjectObj, login);
            if (!tenantProject.getStatusCode().is2xxSuccessful()) {
                failCount.getAndIncrement();
            }
            log.info("end of creating project");
        };
        List<Runnable> runnableList = new ArrayList<>();
        IntStream.range(0, numOfConcurrent).forEach(e -> {
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
