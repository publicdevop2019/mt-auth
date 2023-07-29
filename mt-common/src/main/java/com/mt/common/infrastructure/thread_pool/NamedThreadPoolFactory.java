package com.mt.common.infrastructure.thread_pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadPoolFactory implements ThreadFactory {
    private final String name;
    AtomicInteger count = new AtomicInteger(0);

    public NamedThreadPoolFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name + "-thread-" + count.incrementAndGet());
        return thread;
    }

}