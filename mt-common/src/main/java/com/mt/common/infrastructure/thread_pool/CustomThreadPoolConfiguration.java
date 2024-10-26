package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CustomThreadPoolConfiguration {
    @Bean(name = "job")
    public CleanUpThreadPoolExecutor pool() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(8);//fixed queue size
        return new CleanUpThreadPoolExecutor(
            10,
            25,
            1000,
            TimeUnit.SECONDS,
            queue,
            new NamedThreadPoolFactory("job"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.warn("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

    /**
     * create custom thread pool for rabbitmq, to avoid single thread get all queue bindings
     *
     * @return thread pool executor
     */
    @Bean(name = "event-sub")
    public CleanUpThreadPoolExecutor pool2() {
        //for event-pub, unlimited pool size to make sure one channel has one consumer only
        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        return new CleanUpThreadPoolExecutor(
            20,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            queue,
            new NamedThreadPoolFactory("event-sub"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.warn("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

    @Bean(name = "event-pub")
    public CleanUpThreadPoolExecutor pool3() {
        //no queueing, unlimited pool size
        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        return new CleanUpThreadPoolExecutor(
            20,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            queue,
            new NamedThreadPoolFactory("event-pub"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.warn("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

    @Bean(name = "event-exe")
    public CleanUpThreadPoolExecutor pool4() {
        //no queueing, max pool size = 60
        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        return new CleanUpThreadPoolExecutor(
            50,
            100,
            60,
            TimeUnit.SECONDS,
            queue,
            new NamedThreadPoolFactory("event-exe"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.warn("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

    @Bean(name = "mark-event")
    public CleanUpThreadPoolExecutor pool5() {
        //no queueing, max pool size = 60
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(64);
        return new CleanUpThreadPoolExecutor(
            50,
            100,
            60,
            TimeUnit.SECONDS,
            queue,
            new NamedThreadPoolFactory("mark-event"),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.warn("reject " + r.toString() + ", queue.size: " + queue.size());
                }
            }
        );
    }

}
