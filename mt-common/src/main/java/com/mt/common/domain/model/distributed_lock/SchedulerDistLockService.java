package com.mt.common.domain.model.distributed_lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@Slf4j
public class SchedulerDistLockService {
    @Autowired
    private RedissonClient redissonClient;

    public void executeIfLockSuccess(String taskName, Integer lockInSeconds,Consumer<Void> function ) {
        log.trace("before starting scheduler {} job", taskName);
        String key = taskName + "_scheduler_dist_lock";
        RLock lock = redissonClient.getLock(key);
        boolean locked = lock.isLocked();
        log.trace("current lock is {}", locked ? "locked" : "unlocked");
        boolean b;
        try {
            b = lock.tryLock(0, lockInSeconds, TimeUnit.SECONDS);
            if (b) {
                try {
                    log.trace("acquire lock success for {}", key);
                    function.accept(null);
                } finally {
                    lock.unlock();
                    log.trace("release lock with key: {}", key);
                }
            } else {
                log.trace("acquire lock failed for {}", key);
            }
        } catch (InterruptedException e) {
            log.error("error during acquire lock", e);
        }
    }
}
