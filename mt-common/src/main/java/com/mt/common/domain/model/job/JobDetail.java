package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
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
public class JobDetail extends Auditable implements Serializable {
    @Setter(AccessLevel.PRIVATE)
    private String name;
    @Convert(converter = JobStatus.DbConverter.class)
    private JobStatus lastStatus;
    @Convert(converter = JobType.DbConverter.class)
    private JobType type;
    @Convert(converter = JobStrategy.DbConverter.class)
    private JobStrategy executeStrategy;
    private int failureCount;
    private int lockAcquireFailureCount;
    private int maxLockAcquireFailureAllowed;
    private String failureReason;
    private int failureAllowed;
    @Setter
    private boolean notifiedAdmin;
    private Integer lockInSec;
    private Integer initLockInSec;
    private Integer maxLockInSec;
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
        this.lockInSec = increaseLockTime();
        this.lastStatus = JobStatus.FAILURE;
        return isPaused();
    }

    private int increaseLockTime() {
        int i = Math.floorDiv(this.maxLockInSec - this.lockInSec, this.failureAllowed);
        i = i == 0 ? 1 : i;//minimum step is 1 second
        int i1 = this.lockInSec + i;
        return i1 > this.maxLockInSec ? this.maxLockInSec : i1;
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
    }

    public void syncWithJob(JobDetail jd) {
        this.failureCount = jd.failureCount;
        this.failureReason = jd.failureReason;
        this.lastExecution = jd.lastExecution;
        this.lastStatus = jd.lastStatus;
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
        this.lockInSec = this.initLockInSec;
    }
}
