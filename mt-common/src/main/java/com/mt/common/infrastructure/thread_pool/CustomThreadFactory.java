package com.mt.common.infrastructure.thread_pool;

import com.rabbitmq.client.UnblockedCallback;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class CustomThreadFactory implements ThreadFactory {
    AtomicInteger count = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("custom-thread-" + count.incrementAndGet());
        return thread;
    }

}
