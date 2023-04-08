package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.DistributedJobService;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import com.mt.common.domain.model.job.event.JobThreadStarvingEvent;
import com.mt.common.infrastructure.thread_pool.CustomThreadPoolExecutor;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisDistributedJobService implements DistributedJobService {
    @Autowired
    private CustomThreadPoolExecutor taskExecutor;
    @Autowired
    private RedissonClient redissonClient;
    @Value("${instanceId}")
    private long instanceId;
    private final ConcurrentHashMap<String, Integer> jobInstanceFailureCountMap =
        new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> jobInstanceNotificationMap =
        new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> jobIgnoreCount =
        new ConcurrentHashMap<>();

    /**
     * execute job which do not write to database, e.g. sending data to MQ
     *
     * @param jobName job name
     * @param jobFn   job function
     */
    @Override
    public void execute(String jobName, Runnable jobFn, boolean transactional, int ignoreCount) {
        Integer orDefault = jobIgnoreCount.getOrDefault(jobName, ignoreCount);
        if (orDefault != 0) {
            jobIgnoreCount.put(jobName, --orDefault);
            return;
        }
        taskExecutor.execute(() -> {
            //check if job exist
            Optional<JobDetail> byName =
                CommonDomainRegistry.getJobRepository().getByName(jobName);
            if (byName.isEmpty()) {
                log.error("job {} not found", jobName);
                CommonDomainRegistry.getTransactionService().transactional(() -> {
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new JobNotFoundEvent(jobName));
                });
                return;
            }
            JobDetail job = byName.get();
            if (job.getType().equals(JobType.CLUSTER) && job.isPaused()) {
                return;
            }
            if (job.getType().equals(JobType.CLUSTER)) {
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
                //if job is per instance
                JobDetail template = JobDetail.cloneJobFrom(job, instanceId);
                Optional<JobDetail> instanceJob =
                    CommonDomainRegistry.getJobRepository()
                        .getByName(template.getName());
                if (instanceJob.isPresent() && instanceJob.get().isPaused()) {
                    return;
                }
                JobDetail jobDetail = instanceJob.orElse(template);
                jobWrapper(jobFn, transactional, jobDetail);
            }
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

    private static void jobWrapper(Runnable jobFn, boolean transactional,
                                   JobDetail job) {
        if (transactional) {
            CommonDomainRegistry.getTransactionService()
                .transactional(() -> {
                    boolean b = doJob(job.getName(), jobFn);
                    updateJobStatus(b, job);
                    CommonDomainRegistry.getJobRepository().store(job);
                });
        } else {
            boolean b = doJob(job.getName(), jobFn);
            //update instance job status
            CommonDomainRegistry.getTransactionService()
                .transactional(() -> {
                    updateJobStatus(b, job);
                    CommonDomainRegistry.getJobRepository().store(job);
                });
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
                                      Runnable function, boolean transactional) {
        log.trace("before starting scheduler {} job", jobName);
        String key = getJobLockKey(jobName);
        RLock lock = redissonClient.getLock(key);
        //"if (lock.tryLock())" guarantee atomic operation
        if (lock.tryLock()) {
            log.trace("acquire lock success for key {}", key);
            // release lock after below steps complete to avoid concurrent update issue
            // 1. job execution success or error
            // 2. job status committed
            try {
                JobDetail jobDetail =
                    CommonDomainRegistry.getJobRepository().getById(jobId).get();
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

    private static void updateJobStatus(boolean finalJobSuccess, JobDetail jobDetail) {
        if (finalJobSuccess) {
            jobDetail.executeSuccess();
        } else {
            DomainEvent domainEvent = jobDetail.handleJobExecutionException();
            if (domainEvent == null) {
                return;
            }
            jobDetail.setNotifiedAdmin(true);
            CommonDomainRegistry.getDomainEventRepository()
                .append(domainEvent);
        }

    }

    private static boolean doJob(String jobName, Runnable function) {
        boolean jobSuccess = false;
        try {
            function.run();
            jobSuccess = true;
        } catch (Exception ex) {
            log.error("job {} execute fail, updating status", jobName, ex);
        }
        return jobSuccess;
    }

    private static String getJobLockKey(String jobName) {
        return jobName + "_sync_scheduler_dist_lock";
    }

    private static void checkJobStarving(String jobName, JobDetail job) {
        //check if job was executed in time, otherwise send notification
        if (job.notifyJobStarving()) {
            log.warn("job {} exceed max idle time, last execution time {}",
                jobName, job.getLastExecution().getTime());
            if (!job.isNotifiedAdmin()) {
                log.info("creating {} JobStarvingEvent", jobName);
                CommonDomainRegistry.getTransactionService()
                    .transactional(() -> {
                        JobStarvingEvent starvingEvent =
                            new JobStarvingEvent(job);
                        CommonDomainRegistry.getDomainEventRepository()
                            .append(starvingEvent);
                        CommonDomainRegistry.getJobRepository()
                            .notifyAdmin(job.getJobId());
                    });
            }
        }
    }

    private void checkThreadStarving(String jobName, JobDetail job, Integer failureCountOrDefault) {
        if (failureCountOrDefault > job.getMaxLockAcquireFailureAllowed() &&
            !jobInstanceNotificationMap.getOrDefault(jobName, false)) {
            CommonDomainRegistry.getTransactionService().transactional(
                () -> {
                    log.warn(
                        "job {} thread unable to acquire lock multiple times",
                        jobName);
                    JobThreadStarvingEvent starvingEvent =
                        new JobThreadStarvingEvent(job, instanceId);
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(starvingEvent);
                });
            jobInstanceNotificationMap.put(jobName, true);
        }
    }

}
