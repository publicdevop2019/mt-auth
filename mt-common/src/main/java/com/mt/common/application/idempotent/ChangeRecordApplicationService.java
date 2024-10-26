package com.mt.common.application.idempotent;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import org.springframework.stereotype.Service;

@Service
public class ChangeRecordApplicationService {

    public ChangeRecord saveChange(String changeId, String aggregate) {
        ChangeRecord changeRecord = ChangeRecord.create(changeId, aggregate);
        CommonDomainRegistry.getChangeRecordRepository().add(changeRecord);
        return changeRecord;
    }

    public ChangeRecord saveEmptyChange(String changeId, String aggregate) {
        ChangeRecord emptyBackwardChange = ChangeRecord.create(changeId, aggregate);
        emptyBackwardChange.setEmptyOpt(Boolean.TRUE);
        //create empty forward change for concurrent safe
        ChangeRecord emptyForwardChange =
            ChangeRecord.create(ChangeRecord.getForwardChangeId(changeId), aggregate);
        emptyForwardChange.setEmptyOpt(Boolean.TRUE);
        CommonDomainRegistry.getChangeRecordRepository().add(emptyBackwardChange);
        CommonDomainRegistry.getChangeRecordRepository().add(emptyForwardChange);
        return emptyBackwardChange;
    }
}
