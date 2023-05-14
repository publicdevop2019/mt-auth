package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobThreadFactory implements ThreadFactory {
    AtomicInteger count = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("job-thread-" + count.incrementAndGet());
        return thread;
    }

}
