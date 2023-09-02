package com.mt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * mt-auth access module.
 */

@SpringBootApplication
@EnableEurekaServer
public class Access {
    /**
     * application entry.
     *
     * @param args initial arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Access.class, args);
    }
}

