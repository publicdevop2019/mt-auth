package com.mt.access.application.report;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.report.AccessRecord;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ReportApplicationService {

    @Transactional
    public void uploadReport(List<String> records, String instanceId, String name) {
        records.forEach(e->{
            AccessRecord accessRecord = new AccessRecord(name, instanceId, e);
            DomainRegistry.getAccessRecordRepository().add(accessRecord);
        });
    }
}
