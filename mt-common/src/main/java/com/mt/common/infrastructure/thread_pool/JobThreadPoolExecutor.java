package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class JobThreadPoolExecutor extends ThreadPoolExecutor {

    public JobThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 BlockingQueue<Runnable> workQueue,
                                 JobThreadFactory threadFactory,
                                 RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
            handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        log.trace("clean up after execute");
        MDC.clear();
    }
}
