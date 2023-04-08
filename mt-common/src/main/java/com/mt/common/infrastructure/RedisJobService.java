package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobService;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import com.mt.common.domain.model.job.event.JobThreadStarvingEvent;
import com.mt.common.infrastructure.thread_pool.CustomThreadPoolExecutor;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisJobService implements JobService {
    private static final String LOCK_LOST = "lock lost";
    @Autowired
    private CustomThreadPoolExecutor taskExecutor;
    @Autowired
    private RedissonClient redissonClient;
    @Value("${instanceId}")
    private long instanceId;
    private final ConcurrentHashMap<String, Integer> jobThreadFailureCountMap =
        new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> jobThreadNotificationMap =
        new ConcurrentHashMap<>();

    @Override
    public void execute(String jobName, Runnable jobFn) {
        taskExecutor.execute(() -> {
            Optional<JobDetail> byName =
                CommonDomainRegistry.getJobRepository().getByName(jobName);
            byName.ifPresentOrElse((job) -> {
                if (job.getType().equals(JobType.CLUSTER) && !job.isPaused()) {
                    try {
                        boolean b = syncExecute(jobName, job.getJobId(), jobFn);
                        if (!b) {
                            //when thread cannot get lock, updating job is not allowed to avoid concurrent update issue
                            Integer failureCountOrDefault =
                                jobThreadFailureCountMap.getOrDefault(jobName, 0);
                            failureCountOrDefault++;
                            jobThreadFailureCountMap.put(jobName, failureCountOrDefault);
                            if (failureCountOrDefault > job.getMaxLockAcquireFailureAllowed() &&
                                !jobThreadNotificationMap.getOrDefault(jobName, false)) {
                                log.warn("job {} thread unable to acquire lock multiple times",
                                    jobName);
                                JobThreadStarvingEvent starvingEvent =
                                    new JobThreadStarvingEvent(job, instanceId);
                                CommonDomainRegistry.getTransactionService().transactional(
                                    () -> CommonDomainRegistry.getDomainEventRepository()
                                        .append(starvingEvent));
                                jobThreadNotificationMap.put(jobName, true);
                            }
                            //check if job was executed in time, otherwise send notification
                            if (job.notifyJobStarving()) {
                                log.warn("job {} exceed max idle time, last execution time {}",
                                    jobName, job.getLastExecution().getTime());
                                if (!job.isNotifiedAdmin()) {
                                    log.info("creating {} JobStarvingEvent", jobName);
                                    JobStarvingEvent starvingEvent =
                                        new JobStarvingEvent(job);
                                    CommonDomainRegistry.getTransactionService()
                                        .transactional(() -> {
                                            CommonDomainRegistry.getDomainEventRepository()
                                                .append(starvingEvent);
                                            CommonDomainRegistry.getJobRepository()
                                                .notifyAdmin(job.getJobId());
                                        });
                                }
                            }
                        } else {
                            jobThreadFailureCountMap.remove(jobName);
                            jobThreadNotificationMap.remove(jobName);
                        }
                    } catch (DefinedRuntimeException ex) {
                        log.warn("error during job {} execution", jobName, ex);
                        if (LOCK_LOST.equalsIgnoreCase(ex.getMessage())) {
                            DomainEvent domainEvent = job.handleLockLostException();
                            sendNotification(job, domainEvent);
                        } else {
                            DomainEvent domainEvent = job.handleJobExecutionException();
                            sendNotification(job, domainEvent);
                        }
                    }
                } else {
                    if (!job.isPaused()) {
                        CommonDomainRegistry.getTransactionService()
                            .transactional(() -> {
                                JobDetail instanceClone = JobDetail.cloneJobFrom(job, instanceId);
                                Optional<JobDetail> instanceJobWrapper =
                                    CommonDomainRegistry.getJobRepository()
                                        .getByName(instanceClone.getName());
                                JobDetail jobDetail = instanceJobWrapper.orElse(instanceClone);
                                DomainEvent event = null;
                                try {
                                    jobFn.run();
                                    jobDetail.executeSuccess();
                                } catch (Exception ex) {
                                    log.error("error during job execution", ex);
                                    event = jobDetail.handleJobExecutionException();
                                }
                                sendNotification(jobDetail, event);
                                CommonDomainRegistry.getJobRepository().store(jobDetail);
                            });
                    } else {
                        log.warn("job is paused {}", jobName);
                    }
                }
            }, () -> {
                log.error("job not found! name-> {}", jobName);
                CommonDomainRegistry.getTransactionService().transactional(() -> {
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new JobNotFoundEvent(jobName));
                });
                log.warn("notify admin about job not found");
            });

        });


    }

    @Override
    public void reset(String jobName) {
        log.trace("resetting job {}", jobName);
        String key = getJobLockKey(jobName);
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            log.warn("job {} is locked, forcing unlock", jobName);
            lock.forceUnlock();
        }
    }

    /**
     * send notification to admin, ignore if domain event is null
     *
     * @param job   current job
     * @param event event to be sent
     */
    private void sendNotification(JobDetail job,
                                  @Nullable DomainEvent event) {
        if (event == null) {
            return;
        }
        CommonDomainRegistry.getTransactionService().transactional(() -> {
            CommonDomainRegistry.getJobRepository()
                .notifyAdmin(job.getJobId());
            CommonDomainRegistry.getDomainEventRepository()
                .append(event);
        });
    }

    /**
     * execute job and update its status if lock acquire success, also check if lock released before job complete
     *
     * @param jobName  lock key name
     * @param jobId    job id
     * @param function job
     */
    private boolean syncExecute(String jobName, JobId jobId,
                                Runnable function) {
        log.trace("before starting scheduler {} job", jobName);
        String key = getJobLockKey(jobName);
        RLock lock = redissonClient.getLock(key);
        //"if (lock.tryLock())" guarantee atomic operation
        if (lock.tryLock()) {
            log.trace("acquire lock success for key {}", key);
            // release lock after
            // 1. job execution success or error
            // 2. job updated success or error
            try {
                function.run();
                log.trace("job {} execute success, updating status", jobName);
                CommonDomainRegistry.getTransactionService().transactional(() -> {
                    JobDetail jobDetail =
                        CommonDomainRegistry.getJobRepository().getById(jobId).get();
                    jobDetail.executeSuccess();
                });
            } catch (Exception exception) {
                throw new DefinedRuntimeException("error during execution", "0058",
                    HttpResponseCode.NOT_HTTP,
                    ExceptionCatalog.OPERATION_ERROR, exception);
            } finally {
                if (!lock.isHeldByCurrentThread()) {
                    log.error("lock not hold by current thread, this should not happen");
                }
                lock.unlock();
                log.trace("release {} lock success", jobName);
            }
        } else {
            log.info("ignore {} job due to lock is busy", jobName);
            if (lock.isHeldByCurrentThread()) {
                log.error("lock is not released properly, this should not happen");
            }
            return false;
        }
        return true;
    }

    private static String getJobLockKey(String jobName) {
        return jobName + "_sync_scheduler_dist_lock";
    }

}
