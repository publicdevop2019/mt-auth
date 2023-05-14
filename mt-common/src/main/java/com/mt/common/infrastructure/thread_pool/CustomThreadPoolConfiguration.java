package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CustomThreadPoolConfiguration {
    @Bean
    public JobThreadPoolExecutor pool() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);//fixed queue size
        return new JobThreadPoolExecutor(
            10,
            25,
            1000,
            TimeUnit.SECONDS,
            queue,
            new JobThreadFactory(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.error("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

    @Bean(name = "msg")
    public ThreadPoolExecutor pool2() {
        //for msg queue we give it max value to avoid rejection
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(
            20,
            20,
            1000,
            TimeUnit.SECONDS,
            queue,
            new MsgThreadFactory(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.debug("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

}
