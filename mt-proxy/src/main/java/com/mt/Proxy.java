package com.mt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Proxy {
    public static void main(String[] args) {
        SpringApplication.run(Proxy.class, args);
    }
}
