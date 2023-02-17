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
    NotificationMngmtWsHandler mngmtHandler;

    @Autowired
    NotificationUserWsHandler userHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
            .addHandler(mngmtHandler, "/monitor").setAllowedOrigins("*")
        ;
        webSocketHandlerRegistry
            .addHandler(userHandler, "/user/monitor").setAllowedOrigins("*")
        ;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

}
