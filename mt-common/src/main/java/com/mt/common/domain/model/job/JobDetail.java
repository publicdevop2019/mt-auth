package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class JobDetail {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    @Convert(converter = JobName.DbConverter.class)
    private JobName name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastExecution;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private JobId jobId;

    private JobDetail(JobName name, Date lastExecution, JobId jobId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.name = name;
        this.lastExecution = lastExecution;
        this.jobId = jobId;
    }

    public static JobDetail dataValidation() {
        return new JobDetail(JobName.DATA_VALIDATION, Date.from(Instant.now()), new JobId());
    }

    public static JobDetail proxyValidation() {
        return new JobDetail(JobName.PROXY_VALIDATION, Date.from(Instant.now()), new JobId());
    }

    public static JobDetail wsRenew() {
        return new JobDetail(JobName.KEEP_WS_CONNECTION, Date.from(Instant.now()), new JobId());
    }

    public static JobDetail eventScan() {
        return new JobDetail(JobName.EVENT_SCAN, Date.from(Instant.now()), new JobId());
    }

    public static JobDetail missingEventScan() {
        return new JobDetail(JobName.MISSED_EVENT_SCAN, Date.from(Instant.now()), new JobId());
    }

    public void updateStatus(JobDetail jobDetail) {
        this.lastExecution = jobDetail.getLastExecution();
    }
}
