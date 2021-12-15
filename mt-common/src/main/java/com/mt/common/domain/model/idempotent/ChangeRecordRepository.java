package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface ChangeRecordRepository {
    SumPagedRep<ChangeRecord> changeRecordsOfQuery(ChangeRecordQuery changeRecordQuery);

    void addForwardChangeRecord(ChangeRecord changeRecord);

    void addReverseChangeRecord(ChangeRecord changeRecord);

    void addEmptyReverseChangeRecord(ChangeRecord changeRecord);

    void addEmptyForwardChangeRecord(ChangeRecord changeRecord);

}
