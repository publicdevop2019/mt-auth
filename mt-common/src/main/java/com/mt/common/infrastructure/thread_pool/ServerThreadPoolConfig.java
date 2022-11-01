package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Slf4j
@Configuration
public class ServerThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        log.debug("creating custom thread pool with custom factory");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadFactory(threadFactory());
        return threadPoolTaskExecutor;
    }

    @Bean
    public ThreadFactory threadFactory() {
        return new CustomThreadFactory();
    }
}
