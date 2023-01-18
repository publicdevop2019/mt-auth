package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobService;
import com.mt.common.domain.model.job.JobStrategy;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import com.mt.common.infrastructure.thread_pool.CustomThreadPoolExecutor;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisJobService implements JobService {
    @Autowired
    private CustomThreadPoolExecutor taskExecutor;
    @Autowired
    private RedissonClient redissonClient;
    @Value("${instanceId}")
    private long instanceId;

    @Override
    public void execute(String jobName, Runnable jobFn) {
        taskExecutor.execute(() -> {
            Optional<JobDetail> byName =
                CommonDomainRegistry.getJobRepository().getByName(jobName);
            byName.ifPresentOrElse((job) -> {
                if (job.getType().equals(JobType.CLUSTER) && !job.isPaused()) {
                    try {
                        if (job.getExecuteStrategy().equals(JobStrategy.ASYNC)) {
                            asyncExecute(job.getName(), job.getLockInSec(), jobFn);
                            job.executeSuccess();
                        } else {
                            boolean b = syncExecute(job.getName(), jobFn);
                            if (!b) {
                                job.acquireLockFailed();
                                if (job.starving() && !job.isNotifiedAdmin()) {
                                    log.warn(
                                        "notify admin about job starving (unable to get lock multiple times)");
                                    notifyAdmin(job, new JobStarvingEvent(job));
                                }
                            } else {
                                job.executeSuccess();
                            }
                        }
                    } catch (LockLostException ex) {
                        boolean b = job.recordLockFailure();
                        if (b && !job.isNotifiedAdmin()) {
                            log.warn("notify admin about job paused");
                            notifyAdmin(job, new JobPausedEvent(job));
                        }
                    } catch (ExecutionException ex) {
                        boolean b = job.recordJobFailure();
                        if (b && !job.isNotifiedAdmin()) {
                            log.warn("notify admin about job paused");
                            notifyAdmin(job, new JobPausedEvent(job));
                        }
                    }
                    CommonDomainRegistry.getJobRepository().store(job);
                } else {
                    if (!job.isPaused()) {
                        boolean notify = false;
                        try {
                            jobFn.run();
                            job.executeSuccess();
                        } catch (Exception ex) {
                            notify = job.recordJobFailure();

                        }
                        JobDetail jb = JobDetail.instanceJobFrom(job, instanceId);

                        Optional<JobDetail> byName1 = CommonDomainRegistry.getJobRepository()
                            .getByName(jb.getName());
                        boolean finalNotify = notify;
                        byName1.ifPresentOrElse((instanceJob) -> {
                            instanceJob.syncWithJob(jb);
                            if (finalNotify && !instanceJob.isNotifiedAdmin()) {
                                //@Todo notify admin
                                log.warn("notify admin about job paused");
                                instanceJob.setNotifiedAdmin(true);
                            }
                            CommonDomainRegistry.getJobRepository().store(instanceJob);
                        }, () -> CommonDomainRegistry.getJobRepository().store(jb));
                    } else {
                        log.warn("job is paused {}", jobName);
                    }
                }
            }, () -> {
                log.error("job not found! name-> {}", jobName);
                CommonDomainRegistry.getTransactionService().transactional(() -> {
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new JobNotFoundEvent());
                });
                log.warn("notify admin about job not found");
            });

        });


    }

    private void notifyAdmin(JobDetail job,
                             DomainEvent event) {
        CommonDomainRegistry.getTransactionService().transactional(() -> {
            Optional<JobDetail> byId =
                CommonDomainRegistry.getJobRepository().getById(job.getJobId());
            JobDetail jobDetail = byId.get();
            CommonDomainRegistry.getDomainEventRepository()
                .append(event);
            jobDetail.setNotifiedAdmin(true);
        });
    }

    /**
     * execute job if redis lock acquire success, also check if lock released before job complete
     *
     * @param keyName       lock key name
     * @param lockInSeconds lock in seconds
     * @param function      job
     */
    private void asyncExecute(String keyName, Integer lockInSeconds,
                              Runnable function)
        throws ExecutionException, LockLostException {
        log.trace("before starting scheduler {} job", keyName);
        String key = keyName + "_async_scheduler_dist_lock";
        RLock lock = redissonClient.getLock(key);
        boolean locked = lock.isLocked();
        log.trace("current lock is {}", locked ? "locked" : "unlocked");
        boolean b;
        try {
            b = lock.tryLock(0, lockInSeconds, TimeUnit.SECONDS);
            if (b) {
                try {
                    log.trace("acquire lock success for {}", key);
                    function.run();
                } catch (Exception exception) {
                    log.error("exception during job execution, key: {}, error detail: {}", key,
                        exception);
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.trace("release lock with key: {}", key);
                        throw new ExecutionException();
                    } else {
                        throw new LockLostException();
                    }
                }
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.trace("release lock with key: {}", key);
                } else {
                    throw new LockLostException();
                }
            } else {
                log.trace("acquire lock failed for {}", key);
            }
        } catch (InterruptedException e) {
            log.error("error during acquire lock", e);
        }
    }

    /**
     * execute job if redis lock acquire success, also check if lock released before job complete
     *
     * @param jobName  lock key name
     * @param function job
     */
    private boolean syncExecute(String jobName,
                                Runnable function)
        throws ExecutionException, LockLostException {
        log.trace("before starting scheduler {} job", jobName);
        String key = jobName + "_sync_scheduler_dist_lock";
        RLock lock = redissonClient.getLock(key);
        boolean locked = lock.isLocked();
        log.debug("current {} lock is {}", jobName, locked ? "locked" : "unlocked");
        if (!locked) {
            lock.lock();
            log.trace("acquire lock success for key {}", key);
            try {
                function.run();
            } catch (Exception exception) {
                log.error("exception during job execution, job: {}, error detail: {}", jobName,
                    exception);
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.trace("release {} lock success", jobName);
                    throw new ExecutionException();
                } else {
                    throw new LockLostException();
                }
            }
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.trace("release {} lock success", jobName);
            } else {
                throw new LockLostException();
            }
        } else {
            log.info("ignore {} job due to lock is busy", jobName);
            return false;
        }
        return true;
    }


    private static class LockLostException extends Exception {
    }

    private static class ExecutionException extends Exception {
    }
}
