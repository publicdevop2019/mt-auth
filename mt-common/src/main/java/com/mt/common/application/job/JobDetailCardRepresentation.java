package com.mt.common.application.job;

import com.mt.common.domain.model.job.JobDetail;
import java.util.Date;
import lombok.Getter;

@Getter
public class JobDetailCardRepresentation {
    private final String name;
    private final long lastExecution;

    public JobDetailCardRepresentation(JobDetail j) {
        this.name = j.getName().name();
        this.lastExecution = j.getLastExecution().getTime();
    }
}
