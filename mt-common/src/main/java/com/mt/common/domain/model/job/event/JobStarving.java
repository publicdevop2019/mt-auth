package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JobStarving extends DomainEvent {
    public static final String JOB_STARVING = "job_starving";
    public static final String name = "JOB_STARVING";
    private String jobName;
    {

        setTopic(JOB_STARVING);
        setName(name);
    }
    public JobStarving(JobDetail jobDetail) {
        super(jobDetail.getJobId());
        jobName=jobDetail.getName();
    }
}
