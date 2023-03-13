package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.NextAuditable;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "jobRegion")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class JobDetail extends NextAuditable implements Serializable {
    @Setter(AccessLevel.PRIVATE)
    private String name;
    @Convert(converter = JobStatus.DbConverter.class)
    private JobStatus lastStatus;
    @Convert(converter = JobType.DbConverter.class)
    private JobType type;
    private int failureCount;
    private int lockAcquireFailureCount;
    private int maxLockAcquireFailureAllowed;
    private String failureReason;
    private int failureAllowed;
    @Setter
    private boolean notifiedAdmin;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastExecution;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private JobId jobId;

    public static JobDetail cloneJobFrom(JobDetail job, long instanceId) {
        JobDetail jobDetail = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(job);
        jobDetail.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        jobDetail.setJobId(new JobId());
        jobDetail.setName(job.getName() + "_" + instanceId);
        return jobDetail;
    }

    public boolean isPaused() {
        return failureCount >= failureAllowed;
    }

    /**
     * update job after lock failure detected
     *
     * @return if max lock in sec reached
     */
    public boolean recordLockFailure() {
        this.failureCount++;
        failureReason = "LOCK_LOST";
        this.lastExecution = Date.from(Instant.now());
        this.lastStatus = JobStatus.FAILURE;
        return isPaused();
    }

    public boolean recordJobFailure() {
        this.failureCount++;
        failureReason = "JOB_EXECUTION";
        this.lastExecution = Date.from(Instant.now());
        this.lastStatus = JobStatus.FAILURE;
        return isPaused();
    }

    public void executeSuccess() {
        this.failureCount = 0;
        failureReason = null;
        this.lastExecution = Date.from(Instant.now());
        this.lastStatus = JobStatus.SUCCESS;
        this.notifiedAdmin=false;
        this.lockAcquireFailureCount=0;
    }

    public void acquireLockFailed() {
        this.lockAcquireFailureCount++;
    }

    public boolean starving() {
        return this.lockAcquireFailureCount >= maxLockAcquireFailureAllowed;
    }

    public void reset() {
        this.failureCount = 0;
        this.lockAcquireFailureCount = 0;
        this.notifiedAdmin = false;
        this.failureReason = null;
    }
    public DomainEvent handleJobExecutionException(){
        boolean b = recordJobFailure();
        if (b && !isNotifiedAdmin()) {
            log.warn("notify admin about job paused");
            return new JobPausedEvent(this);
        }
        return null;
    }

    public DomainEvent handleLockLostException() {
        boolean b = recordLockFailure();
        if (b && !isNotifiedAdmin()) {
            log.warn("notify admin about job paused");
            return new JobPausedEvent(this);
        }
        return null;
    }

    public DomainEvent handleLockFailedException() {
        acquireLockFailed();
        if (starving() && !isNotifiedAdmin()) {
            log.warn(
                "notify admin about job starving (unable to get lock multiple times)");
           return new JobStarvingEvent(this);
        }
        return null;
    }
}
