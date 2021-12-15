package com.mt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class Messenger {
    public static void main(String[] args) {
        SpringApplication.run(Messenger.class, args);
    }
}
