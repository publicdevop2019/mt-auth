package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobService;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.infrastructure.thread_pool.CustomThreadPoolExecutor;
import java.util.Optional;
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

    @Override
    public void execute(String jobName, Runnable jobFn) {
        taskExecutor.execute(() -> {
            Optional<JobDetail> byName =
                CommonDomainRegistry.getJobRepository().getByName(jobName);
            byName.ifPresentOrElse((job) -> {
                if (job.getType().equals(JobType.CLUSTER) && !job.isPaused()) {
                    try {
                        boolean b = syncExecute(job.getName(), jobFn);
                        if (!b) {
                            DomainEvent domainEvent = job.handleLockFailedException();
                            sendNotification(job, domainEvent);
                        } else {
                            job.executeSuccess();
                        }
                    } catch (DefinedRuntimeException ex) {
                        log.warn("error during job execution", ex);
                        if (LOCK_LOST.equalsIgnoreCase(ex.getMessage())) {
                            DomainEvent domainEvent = job.handleLockLostException();
                            sendNotification(job, domainEvent);
                        } else {
                            DomainEvent domainEvent = job.handleJobExecutionException();
                            sendNotification(job, domainEvent);
                        }
                    }
                    CommonDomainRegistry.getJobRepository().store(job);
                } else {
                    if (!job.isPaused()) {
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
     * @param jobName  lock key name
     * @param function job
     */
    private boolean syncExecute(String jobName,
                                Runnable function) {
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
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.trace("release {} lock success", jobName);
                    throw new DefinedRuntimeException("error during execution", "0058",
                        HttpResponseCode.NOT_HTTP,
                        ExceptionCatalog.OPERATION_ERROR, exception);
                } else {
                    throw new DefinedRuntimeException("error during execution & lock lost", "0059",
                        HttpResponseCode.NOT_HTTP,
                        ExceptionCatalog.OPERATION_ERROR, exception);
                }
            }
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.trace("release {} lock success", jobName);
            } else {
                throw new DefinedRuntimeException(LOCK_LOST, "0060",
                    HttpResponseCode.NOT_HTTP,
                    ExceptionCatalog.OPERATION_ERROR);
            }
        } else {
            log.info("ignore {} job due to lock is busy", jobName);
            return false;
        }
        return true;
    }

}
