package com.hw;

import com.hw.entity.TestResult;
import com.hw.integration.IntegrationTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;

@Slf4j
//@SpringBootApplication
//@EnableScheduling
public class IntegrationTestRunner {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationTestRunner.class, args);
    }

    @Scheduled(fixedRate = 300 * 1000)
    public void runTest() {
        log.info("test started");
        StringBuilder stringBuilder = new StringBuilder();
        Result result = JUnitCore.runClasses(IntegrationTestSuite.class);
        for (Failure failure : result.getFailures()) {
            log.error(failure.toString());
            stringBuilder.append(failure.toString());
        }
        log.info("Tests {}-executed {}-ignored {}-failed elapse-{}ms", result.getRunCount(), result.getIgnoreCount(), result.getFailureCount(), result.getRunTime());
        if (result.wasSuccessful()) {
            log.info("Tests all passed");
        } else {
            log.error("Tests failed, check log");
        }
    }

    @PreDestroy
    public void onExit() {
        log.info("Closing application..");
        LogManager.shutdown();
    }
}