package com.hw;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class IntegrationTestRunner {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationTestRunner.class, args);
    }

    @PreDestroy
    public void onExit() {
        log.info("Closing application..");
        LogManager.shutdown();
    }
}