package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class ReportResource {
    /**
     * proxy upload report for later analysis
     * @return void
     */
    @PostMapping(path = "reports/proxy")
    public ResponseEntity<Void> uploadReport(
        @RequestBody List<String> records,
        @RequestHeader("instanceId") String instanceId,
        @RequestHeader("name") String name
    ){
        ApplicationServiceRegistry
            .getReportApplicationService().uploadReport(records,instanceId,name);
        return ResponseEntity.ok().build();
    }
}
