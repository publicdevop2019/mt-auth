package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

/**
 * distributed job entity, should not be cached to avoid wrong state
 */
@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class JobDetail extends Auditable implements Serializable {
    @Setter(AccessLevel.PRIVATE)
    private String name;
    @Enumerated(EnumType.STRING)
    private JobStatus lastStatus;
    @Enumerated(EnumType.STRING)
    private JobType type;
    private int failureCount;
    private int maxLockAcquireFailureAllowed;
    /**
     * milliseconds that allowed for job to be not executed,
     * will send notification to admin when this limit reached
     */
    private long minimumIdleTimeMilli;
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
        this.notifiedAdmin = false;
    }

    public void reset() {
        this.failureCount = 0;
        this.notifiedAdmin = false;
        this.failureReason = null;
    }

    public DomainEvent handleJobExecutionException() {
        boolean b = recordJobFailure();
        if (b && !isNotifiedAdmin()) {
            log.warn("notify admin about job paused");
            return new JobPausedEvent(this);
        }
        return null;
    }

    public boolean notifyJobStarving() {
        long idleTime = System.currentTimeMillis() - this.lastExecution.getTime();
        return idleTime > this.minimumIdleTimeMilli;
    }
}
