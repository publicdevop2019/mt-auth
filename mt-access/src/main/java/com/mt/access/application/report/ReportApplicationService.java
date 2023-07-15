package com.mt.access.application.report;

import static com.mt.access.domain.model.permission.Permission.VIEW_API;
import static com.mt.access.infrastructure.AppConstant.ACCESS_DATA_PROCESSING_JOB_NAME;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.report.EndpointReport;
import com.mt.access.domain.model.report.RawAccessRecord;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReportApplicationService {

    private static final String REPORT_TYPE = "type";

    public void uploadReport(List<String> records, String instanceId, String name) {
        CommonDomainRegistry.getTransactionService().transactionalEvent(context -> {
            records.forEach(e -> {
                RawAccessRecord rawAccessRecord = new RawAccessRecord(name, instanceId, e);
                DomainRegistry.getRawAccessRecordRepository().add(rawAccessRecord);
            });
        });
    }

    public EndpointReport analyze(String projectId, String endpointRawId, String queryParam) {
        DomainRegistry.getPermissionCheckService().canAccess(new ProjectId(projectId), VIEW_API);
        EndpointId endpointId = new EndpointId(endpointRawId);
        Map<String, String> type = QueryUtility.parseQuery(queryParam, REPORT_TYPE);
        AtomicReference<EndpointReport> report = new AtomicReference<>();
        Optional.ofNullable(type.get(REPORT_TYPE)).ifPresent(e -> {
            if (e.equalsIgnoreCase("PAST_FIFTEEN_MINUTES")) {
                report.set(DomainRegistry.getReportGenerateService()
                    .generatePast15MinutesReport(endpointId));
            } else if (e.equalsIgnoreCase("PAST_ONE_HOUR")) {
                report.set(
                    DomainRegistry.getReportGenerateService().generatePast1HourReport(endpointId));
            } else if (e.equalsIgnoreCase("PAST_ONE_DAY")) {
                report.set(
                    DomainRegistry.getReportGenerateService().generatePast1DayReport(endpointId));
            } else if (e.equalsIgnoreCase("ALL_TIME")) {
                report.set(
                    DomainRegistry.getReportGenerateService().generateAllTimeReport(endpointId));
            } else {
                throw new DefinedRuntimeException("unsupported report type", "1018",
                    HttpResponseCode.BAD_REQUEST);
            }
        });
        return report.get();
    }

    @Scheduled(cron = "0 * * ? * *")
    public void rawDataEtl() {
        log.trace("triggered scheduled task 3");
        CommonDomainRegistry.getJobService().execute(ACCESS_DATA_PROCESSING_JOB_NAME,
            (context) -> DomainRegistry.getRawAccessRecordProcessService().process(context), true, 1);
    }
}
