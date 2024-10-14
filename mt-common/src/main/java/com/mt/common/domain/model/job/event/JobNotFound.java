package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobNotFound extends DomainEvent {
    public static final String JOB_NOT_FOUND = "job_not_found";
    public static final String name = "JOB_NOT_FOUND";

    {

        setTopic(JOB_NOT_FOUND);
        setName(name);
    }

    private String jobName;

    public JobNotFound(String jobName) {
        super(new AnyDomainId());
        this.jobName = jobName;
    }
}
