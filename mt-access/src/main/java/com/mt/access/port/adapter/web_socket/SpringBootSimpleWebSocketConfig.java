package com.mt.access.port.adapter.web_socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket
public class SpringBootSimpleWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    NotificationMgmtWsHandler mgmtHandler;

    @Autowired
    NotificationUserWsHandler userHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
            .addHandler(mgmtHandler, "/monitor").setAllowedOrigins("*")
        ;
        webSocketHandlerRegistry
            .addHandler(userHandler, "/monitor/user").setAllowedOrigins("*")
        ;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

}
