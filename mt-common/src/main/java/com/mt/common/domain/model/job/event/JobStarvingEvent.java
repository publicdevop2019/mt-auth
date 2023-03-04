package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JobStarvingEvent extends DomainEvent {
    public static final String JOB_STARVING = "job_starving";
    public static final String name = "JOB_STARVING";
    {

        setTopic(JOB_STARVING);
        setName(name);
    }
    public JobStarvingEvent(JobDetail jobDetail) {
        super(jobDetail.getJobId());
    }
}
