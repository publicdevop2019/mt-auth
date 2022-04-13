package com.mt.common.resource;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.job.JobDetailCardRepresentation;
import com.mt.common.domain.model.job.JobDetail;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class JobResource {
    @GetMapping("mngmt/jobs")
    public ResponseEntity<List<JobDetailCardRepresentation>> getAllJobs() {
        Set<JobDetail> jobDetails =
            CommonApplicationServiceRegistry.getJobApplicationService().currentJobs();
        Set<JobDetailCardRepresentation> collect =
            jobDetails.stream().map(JobDetailCardRepresentation::new).collect(Collectors.toSet());
        List<JobDetailCardRepresentation> collect1 =
            collect.stream().sorted(Comparator.comparing(JobDetailCardRepresentation::getName))
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(collect1);
    }
}
