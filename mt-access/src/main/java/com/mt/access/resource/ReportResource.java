package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_PARAM_QUERY;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.report.representation.EndpointReportRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.report.EndpointReport;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class ReportResource {
    /**
     * proxy upload report for later analysis
     *
     * @return void
     */
    @PostMapping(path = "reports/proxy")
    public ResponseEntity<Void> uploadReport(
        @RequestBody List<String> records,
        @RequestHeader("instanceId") String instanceId,
        @RequestHeader("name") String name
    ) {
        ApplicationServiceRegistry
            .getReportApplicationService().uploadReport(records, instanceId, name);
        return ResponseEntity.ok().build();
    }

    /**
     * get analysis result for specific endpoint
     *
     * @return analysis result report
     */
    @GetMapping(path = "projects/{projectId}/endpoints/{id}/report")
    public ResponseEntity<EndpointReportRepresentation> retrieveReport(
        @PathVariable(name = "id") String endpointRawId,
        @PathVariable(name = "projectId") String projectId,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
        @RequestParam(value = HTTP_PARAM_QUERY) String queryParam
    ) {
        DomainRegistry.getCurrentUserService().setUser(jwt);
        EndpointReport report = ApplicationServiceRegistry
            .getReportApplicationService().analysisReportFor(projectId, endpointRawId, queryParam);
        return ResponseEntity.ok().body(new EndpointReportRepresentation(report));
    }

}
