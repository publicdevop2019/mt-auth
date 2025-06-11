package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.DistributedJobService;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.job.event.JobNotFound;
import com.mt.common.domain.model.job.event.JobStarving;
import com.mt.common.domain.model.job.event.JobThreadStarving;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisDistributedJobService implements DistributedJobService {
    private final ConcurrentHashMap<String, Integer> jobInstanceFailureCountMap =
        new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> jobInstanceNotificationMap =
        new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> jobIgnoreCount =
        new ConcurrentHashMap<>();
    @Autowired
    @Qualifier("job")
    private ThreadPoolExecutor taskExecutor;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private MeterRegistry meterRegistry;

    private static void jobWrapper(Consumer<TransactionContext> jobFn, boolean transactional,
                                   JobDetail job) {
        if (transactional) {
            transactionalJobWrapper(jobFn, job);
        } else {
            boolean jobSuccess = false;
            try {
                jobFn.accept(null);
                jobSuccess = true;
            } catch (Exception ex) {
                log.error("job {} execute fail, updating status", job.getName(), ex);
            }
            //update instance job status
            boolean finalJobSuccess = jobSuccess;
            CommonDomainRegistry.getTransactionService()
                .transactionalEvent((context) -> {
                    updateJobStatus(finalJobSuccess, job, context);
                });
        }
    }

    private static void transactionalJobWrapper(Consumer<TransactionContext> jobFn,
                                                JobDetail job) {
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent((context) -> {
                boolean jobSuccess = false;
                try {
                    jobFn.accept(context);
                    jobSuccess = true;
                } catch (Exception ex) {
                    log.error("job {} execute fail, updating status", job.getName(), ex);
                }
                updateJobStatus(jobSuccess, job, context);
            });
    }

    private static void updateJobStatus(boolean finalJobSuccess, JobDetail current,
                                        TransactionContext context) {
        if (finalJobSuccess) {
            JobDetail updated = current.executeSuccess();
            CommonDomainRegistry.getJobRepository().update(current, updated);
        } else {
            DomainEvent domainEvent = current.handleJobExecutionException();
            if (domainEvent == null) {
                return;
            }
            current.setNotifiedAdmin(true);
            context
                .append(domainEvent);
        }

    }

    private static String getJobLockKey(String jobName) {
        return jobName + "_sync_scheduler_dist_lock";
    }

    private static void checkJobStarving(String jobName, JobDetail job) {
        //check if job was executed in time, otherwise send notification
        if (job.starving()) {
            log.warn("job {} exceed max idle time, last execution time {}",
                jobName, job.getLastExecution());
            if (!job.getNotifiedAdmin()) {
                log.info("creating {} JobStarvingEvent", jobName);
                CommonDomainRegistry.getTransactionService()
                    .transactionalEvent((context) -> {
                        JobStarving starvingEvent = new JobStarving(job);
                        context.append(starvingEvent);
                        CommonDomainRegistry.getJobRepository().notifyAdmin(job.getJobId());
                    });
            }
        }
    }

    /**
     * execute job which do not write to database, e.g. sending data to MQ
     *
     * @param jobName job name
     * @param jobFn   job function
     */
    @Override
    public void execute(String jobName, Consumer<TransactionContext> jobFn, boolean transactional,
                        int ignoreCount) {
        CommonDomainRegistry.getLogService().initTrace();
        Integer orDefault = jobIgnoreCount.getOrDefault(jobName, ignoreCount);
        if (orDefault != 0) {
            jobIgnoreCount.put(jobName, --orDefault);
            return;
        }
        taskExecutor.execute(() -> {
            CommonDomainRegistry.getLogService().initTrace();
            Analytics start = Analytics.start(Analytics.Type.JOB_EXECUTION);
            Timer.Sample sample = Timer.start(meterRegistry);

            log.info("started job {}", jobName);
            //check if job exist
            Optional<JobDetail> byName =
                CommonDomainRegistry.getJobRepository().getByName(jobName);
            if (byName.isEmpty()) {
                log.error("job {} not found", jobName);
                CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                    context
                        .append(new JobNotFound(jobName));
                });
                return;
            }
            JobDetail job = byName.get();
            if (job.getType().equals(JobType.CLUSTER) && job.isPaused()) {
                return;
            }
            if (job.getType().equals(JobType.CLUSTER)) {
                log.debug("job is per cluster");
                boolean b = tryDistributedJob(jobName, job.getJobId(), jobFn, transactional);
                if (!b) {
                    //when thread cannot get lock, updating job is not allowed to avoid concurrent update issue
                    Integer failureCountOrDefault =
                        jobInstanceFailureCountMap.getOrDefault(jobName, 0);
                    failureCountOrDefault++;
                    jobInstanceFailureCountMap.put(jobName, failureCountOrDefault);
                    checkThreadStarving(jobName, job, failureCountOrDefault);
                    checkJobStarving(jobName, job);
                } else {
                    jobInstanceFailureCountMap.remove(jobName);
                    jobInstanceNotificationMap.remove(jobName);
                }
            } else {
                log.debug("job is per instance");
                //if job is per instance
                Long instanceId =
                    ((SnowflakeUniqueIdService) CommonDomainRegistry.getUniqueIdGeneratorService()).getInstanceId();
                log.debug("job instance id {}", instanceId);
                JobDetail clonedJob = JobDetail.cloneJobFrom(job, instanceId);
                Optional<JobDetail> existJob = CommonDomainRegistry.getJobRepository()
                    .getByName(clonedJob.getName());
                if (existJob.isEmpty()) {
                    CommonDomainRegistry.getJobRepository()
                        .add(clonedJob);
                }
                if (existJob.isPresent() && existJob.get().isPaused()) {
                    log.debug("job is paused");
                    return;
                }
                JobDetail jobDetail = existJob.orElse(clonedJob);
                jobWrapper(jobFn, transactional, jobDetail);
            }
            start.stop();
            sample.stop(meterRegistry.timer("job_execution", "name", jobName));
            log.info("finished job {}", jobName);
        });
    }

    @Override
    public void resetLock(String jobName) {
        log.trace("resetting job {}", jobName);
        String key = getJobLockKey(jobName);
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            log.warn("job {} is locked, forcing unlock", jobName);
            lock.forceUnlock();
        } else {
            log.warn("job {} is already unlocked, nothing will happen", jobName);
        }
    }

    /**
     * execute job and update its status if lock acquire success, also check if lock released before job complete
     *
     * @param jobName       lock key name
     * @param jobId         job id
     * @param function      job
     * @param transactional if job need write to database
     */
    private boolean tryDistributedJob(String jobName, JobId jobId,
                                      Consumer<TransactionContext> function,
                                      boolean transactional) {
        log.trace("before starting scheduler {} job", jobName);
        String key = getJobLockKey(jobName);
        try {
            Thread.sleep(new Random().nextInt(100));// for a better fairness
        } catch (InterruptedException e) {
            log.error("error during lock random sleep");
        }
        RLock lock = redissonClient.getLock(key);
        //NOTE: "if (lock.tryLock())" guarantee atomic operation
        if (lock.tryLock()) {
            log.trace("acquire lock success for key {}", key);
            // release lock after below steps complete to avoid concurrent update issue
            // 1. job execution success or error
            // 2. job status committed
            try {
                JobDetail jobDetail =
                    CommonDomainRegistry.getJobRepository().getById(jobId);
                jobWrapper(function, transactional, jobDetail);
            } finally {
                lock.unlock();
                log.trace("release {} lock success", jobName);
            }
            return true;
        } else {
            log.trace("ignore {} job due to lock is busy", jobName);
        }
        return false;
    }

    private void checkThreadStarving(String jobName, JobDetail job, Integer failureCountOrDefault) {
        if (failureCountOrDefault > job.getMaxLockAcquireFailureAllowed() &&
            !jobInstanceNotificationMap.getOrDefault(jobName, false)) {
            CommonDomainRegistry.getTransactionService().transactionalEvent(
                (context) -> {
                    Long instanceId =
                        ((SnowflakeUniqueIdService) CommonDomainRegistry.getUniqueIdGeneratorService()).getInstanceId();
                    log.warn(
                        "job {} thread unable to acquire lock multiple times",
                        jobName);
                    JobThreadStarving starvingEvent =
                        new JobThreadStarving(job, instanceId);
                    context
                        .append(starvingEvent);
                });
            jobInstanceNotificationMap.put(jobName, true);
        }
    }

}
