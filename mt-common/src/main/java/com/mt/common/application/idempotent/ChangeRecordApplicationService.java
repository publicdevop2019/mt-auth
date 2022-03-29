package com.mt.common.application.idempotent;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import com.mt.common.domain.model.idempotent.ChangeRecordQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeRecordApplicationService {
    public SumPagedRep<ChangeRecord> changeRecords(String s) {
        return CommonDomainRegistry.getChangeRecordRepository()
            .changeRecordsOfQuery(new ChangeRecordQuery(s));
    }

    public SumPagedRep<ChangeRecord> changeRecords(String s, String s1, String s2) {
        return CommonDomainRegistry.getChangeRecordRepository()
            .changeRecordsOfQuery(new ChangeRecordQuery(s, s1, s2));
    }

    @Transactional
    public void createForward(CreateChangeRecordCommand changeRecord) {
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        ChangeRecord changeRecord1 =
            new ChangeRecord(id, changeRecord.getChangeId(), changeRecord.getAggregateName(),
                changeRecord.getReturnValue());
        CommonDomainRegistry.getChangeRecordRepository().addForwardChangeRecord(changeRecord1);
    }

    @Transactional
    public void createEmptyForward(CreateChangeRecordCommand changeRecord) {
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        ChangeRecord changeRecord1 =
            new ChangeRecord(id, changeRecord.getChangeId(), changeRecord.getAggregateName(),
                changeRecord.getReturnValue());
        CommonDomainRegistry.getChangeRecordRepository().addEmptyForwardChangeRecord(changeRecord1);
    }

    @Transactional
    public void createReverse(CreateChangeRecordCommand changeRecord) {
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        ChangeRecord changeRecord1 =
            new ChangeRecord(id, changeRecord.getChangeId(), changeRecord.getAggregateName(),
                changeRecord.getReturnValue());
        CommonDomainRegistry.getChangeRecordRepository().addReverseChangeRecord(changeRecord1);
    }

    @Transactional
    public void createEmptyReverse(CreateChangeRecordCommand changeRecord) {
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        ChangeRecord changeRecord1 =
            new ChangeRecord(id, changeRecord.getChangeId(), changeRecord.getAggregateName(),
                changeRecord.getReturnValue());
        CommonDomainRegistry.getChangeRecordRepository().addEmptyReverseChangeRecord(changeRecord1);
    }
}
