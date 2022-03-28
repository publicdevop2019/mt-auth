package com.mt.common.infrastructure;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SchedulerTaskPoolConfiguration {
    @Bean
    public CleanUpThreadPoolExecutor executor() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);
        return new CleanUpThreadPoolExecutor(
            10,
            25,
            1000,
            TimeUnit.SECONDS,
            queue,
            Executors.defaultThreadFactory(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.debug("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

}
