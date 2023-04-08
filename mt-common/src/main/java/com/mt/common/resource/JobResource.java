package com.mt.common.resource;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.job.JobDetailCardRepresentation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class JobResource {
    @GetMapping("mgmt/jobs")
    public ResponseEntity<List<JobDetailCardRepresentation>> getAllJobs() {
        return ResponseEntity
            .ok(CommonApplicationServiceRegistry.getJobApplicationService().currentJobs());
    }

    @PostMapping("mgmt/jobs/{id}/reset")
    public ResponseEntity<Void> resetJob(@PathVariable(name = "id") String id) {
        CommonApplicationServiceRegistry.getJobApplicationService().resetJob(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("mgmt/jobs/{id}/reset/lock")
    public ResponseEntity<Void> resetJobLock(@PathVariable(name = "id") String id) {
        CommonApplicationServiceRegistry.getJobApplicationService().resetJobLock(id);
        return ResponseEntity.ok().build();
    }
}
